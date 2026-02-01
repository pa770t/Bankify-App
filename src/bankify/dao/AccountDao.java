package bankify.dao;

import java.sql.*;
import bankify.Account;
import bankify.Agent;
import bankify.Customer;
import bankify.DBConnection;

public class AccountDao {

    private Connection conn;

    public AccountDao(Connection conn) {
        this.conn = conn;
    }

    public boolean createAccount(Customer customer) {
        String sql = "INSERT INTO account (customer_id, account_number, balance) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customer.getCustomerId());
            pstmt.setString(2, customer.getPhoneNumber());
            pstmt.setDouble(3, 0.0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error while creating account: " + e.getMessage());
        }
        return false;
    }

    public boolean createAccount(Agent agent) {
        String sql = "INSERT INTO account (employee_id, account_number, balance, account_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, agent.getAgentId());
            pstmt.setString(2, agent.getPhoneNumber());
            pstmt.setDouble(3, 100000);
            pstmt.setString(4, "AGENT");

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error while creating account: " + e.getMessage());
        }
        return false;
    }

    public Account getAccountById(long accountId) throws SQLException {
        String sql = "SELECT * FROM account WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM account WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public Account getAccountByCustomerId(long customerId) throws SQLException {
        String sql = "SELECT * FROM account WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public Account getAccountByAgentId(long agentId) throws SQLException {
        String sql = "SELECT * FROM account WHERE employee_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, agentId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public Account[] getTwoAccountsForUpdate(long senderId, String receiverAccountNumber) throws SQLException {
        String sql = "SELECT * FROM account WHERE (customer_id = ? OR account_number = ?) ORDER BY account_id FOR UPDATE";
        Account sender = null, receiver = null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, senderId);
            ps.setString(2, receiverAccountNumber);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Account acc = map(rs);
                if (acc.getCustomerId() == senderId) sender = acc;
                else receiver = acc;
            }
        }
        return new Account[]{sender, receiver};
    }

    public Account getAccountByCustomerIdForUpdate(long customerId) throws SQLException {
        String sql = "SELECT * FROM account WHERE customer_id = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public Account getAccountByNumberForUpdate(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM account WHERE account_number = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }

    public void updateBalance(long accountId, double newBalance) throws SQLException {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setLong(2, accountId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(long accountId, String newStatus) throws SQLException {
        String sql = "UPDATE account SET status = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, accountId);
            ps.executeUpdate();
        }
    }

    public void deleteAccount(long accountId) throws SQLException {
        String sql = "DELETE FROM account WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.executeUpdate();
        }
    }

    public boolean deactivateAccount(long accountId) {
        String sql = "UPDATE account SET status = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "CLOSED");
            ps.setLong(2, accountId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean activateAccount(long accountId) {
        String sql = "UPDATE account SET status = ? WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "ACTIVE");
            ps.setLong(2, accountId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Account map(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("account_id"),
                rs.getLong("customer_id"),
                rs.getLong("employee_id"),
                rs.getString("account_number"),
                rs.getDouble("balance"),
                rs.getString("account_type"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
