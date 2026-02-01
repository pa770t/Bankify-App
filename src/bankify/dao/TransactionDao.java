package bankify.dao;

import bankify.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    private Connection conn;

    public TransactionDao(Connection conn) {
        this.conn = conn; // use the same connection for all operations
    }

    /**
     * Create a transaction for a customer/agent
     * Must be used inside an existing transaction to avoid deadlocks
     */
    public boolean createTransaction(long customerId, long agentId, String transactionType,
                                     double amount, String status, String description) throws SQLException {

        // Use the connection passed in, do NOT open a new connection
        String sql = "INSERT INTO transactions " +
                "(account_id, employee_id, transaction_type, amount, status, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Get account (must be already locked in calling transaction)
        Account account = new AccountDao(conn).getAccountByCustomerId(customerId);
        if (account == null) throw new SQLException("Account not found for customerId: " + customerId);

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, account.getAccountId());

            if (agentId == 0) {
                pstmt.setNull(2, java.sql.Types.BIGINT);
            } else {
                pstmt.setLong(2, agentId);
            }

            pstmt.setString(3, transactionType);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, status);
            pstmt.setString(6, description);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get transaction by ID
     */
    public Transaction getTransaction(long transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get all transactions for a customer
     */
    public List<Transaction> getAllTransactionByCustomer(long customerId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.* FROM transactions t " +
                "JOIN account a ON t.account_id = a.account_id " +
                "WHERE a.customer_id = ? " +
                "ORDER BY t.transaction_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /**
     * Helper to map ResultSet to Transaction object
     */
    private Transaction map(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("transaction_id"),
                rs.getLong("account_id"),
                rs.getLong("employee_id"),
                rs.getString("transaction_type"),
                rs.getDouble("amount"),
                rs.getString("status"),
                rs.getTimestamp("transaction_at"),
                rs.getString("description")
        );
    }
}
