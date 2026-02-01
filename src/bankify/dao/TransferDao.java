package bankify.dao;

import bankify.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TransferDao {
    private Connection conn;

    public TransferDao(Connection conn) {
        this.conn = conn;
    }

    public Transaction createTransfer(Customer customer,
                                      long toAccountNumber,
                                      double amount,
                                      String description) {

        int retries = 3; // retry on deadlock

        while (retries-- > 0) {
            try {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                AccountDao accountDao = new AccountDao(conn);
                TransactionDao transactionDao = new TransactionDao(conn);

                // -----------------------------
                // 1️⃣ Lock both accounts in consistent order
                // -----------------------------
                Account[] accounts = accountDao.getTwoAccountsForUpdate(
                        customer.getCustomerId(),
                        String.valueOf(toAccountNumber)
                );

                Account fromAccount = accounts[0];
                Account toAccount   = accounts[1];

                if (fromAccount == null || toAccount == null) {
                    conn.rollback();
                    System.err.println("Sender or receiver account not found");
                    return null;
                }

                // -----------------------------
                // 2️⃣ Check sender balance
                // -----------------------------
                if (fromAccount.getBalance() < amount) {
                    conn.rollback();
                    System.err.println("Insufficient funds");
                    return null;
                }

                // -----------------------------
                // 3️⃣ Insert transfer record
                // -----------------------------
                long transferId;
                String insertTransferSql =
                        "INSERT INTO transfers (from_id, to_account_number, amount, description, status) " +
                                "VALUES (?, ?, ?, ?, 'SUCCESS')";

                try (PreparedStatement ps = conn.prepareStatement(insertTransferSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, customer.getCustomerId());
                    ps.setString(2, String.valueOf(toAccountNumber));
                    ps.setDouble(3, amount);
                    ps.setString(4, description);
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    transferId = rs.getLong(1);
                }

                // -----------------------------
                // 4️⃣ Update balances
                // -----------------------------
                accountDao.updateBalance(fromAccount.getAccountId(), fromAccount.getBalance() - amount);
                accountDao.updateBalance(toAccount.getAccountId(), toAccount.getBalance() + amount);

                // -----------------------------
                // 5️⃣ Insert transactions
                // -----------------------------
                // Sender transaction
                long senderTxId;
                String senderTxSql =
                        "INSERT INTO transactions (account_id, employee_id, transaction_type, amount, status, description) " +
                                "VALUES (?, NULL, 'SEND', ?, 'SUCCESS', ?)";

                try (PreparedStatement ps = conn.prepareStatement(senderTxSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, fromAccount.getAccountId());
                    ps.setDouble(2, amount);
                    ps.setString(3, description);
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    rs.next();
                    senderTxId = rs.getLong(1);
                }

                // Receiver transaction
                transactionDao.createTransaction(
                        toAccount.getCustomerId(),
                        0,
                        "RECEIVE",
                        amount,
                        "SUCCESS",
                        description
                );

                // -----------------------------
                // 6️⃣ Commit everything
                // -----------------------------
                conn.commit();

                // return sender's transaction object
                return transactionDao.getTransaction(senderTxId);

            } catch (SQLException e) {
                try { conn.rollback(); } catch (SQLException ignored) {}

                // 🔁 Deadlock retry
                if (e.getErrorCode() == 1213 || e.getErrorCode() == 1205) {
                    System.err.println("Deadlock detected, retrying...");
                    try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    continue;
                }

                System.err.println("Transfer failed: " + e.getMessage());
                return null;

            } finally {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }

        throw new RuntimeException("Transfer failed after retries");
    }


    public List<Transfer> getAllTransfersByCustomer(long customerId) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT t.* FROM transfers t " +
                "JOIN account a ON t.account_id = a.account_id " +
                "WHERE a.customer_id = ? " +
                "ORDER BY t.transaction_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transfer tf = new Transfer();
                    tf.setTransferId(rs.getLong("transfer_id"));

                    list.add(tf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
