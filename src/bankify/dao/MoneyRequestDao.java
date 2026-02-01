package bankify.dao;

import bankify.Account;
import bankify.Agent;
import bankify.Customer;
import bankify.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MoneyRequestDao {
    private final Connection conn;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    public MoneyRequestDao(Connection conn) {
        this.conn = conn;
        this.accountDao = new AccountDao(conn);
        this.transactionDao = new TransactionDao(conn);
    }

    public void createMoneyRequest(long customer_id, long agent_id, String request_type, double amount,
                                   String description) {
        String sql = "INSERT INTO money_requests (request_from, agent_id, request_type, amount, description) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customer_id);
            stmt.setLong(2, agent_id);
            stmt.setString(3, request_type);
            stmt.setDouble(4, amount);
            stmt.setString(5, description);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    public void acceptOrDenyRequest(long request_id, String status) {
        if (status.equals("ACCEPT")) {
            RequestItem requestItem = getRequestById(request_id);
            Customer customer = new CustomerDao(this.conn).findById(requestItem.request_from);
            Agent agent = new AgentDao(this.conn).findById(requestItem.agent_id);

            Account customer_account;
            Account agent_account;
            if (requestItem.request_type.equals("DEPOSIT")) {
                String sql = "UPDATE money_requests SET status = ? WHERE request_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, status);
                    stmt.setLong(2, request_id);
                    stmt.executeUpdate();

                    customer_account = accountDao.getAccountByCustomerId(customer.getCustomerId());
                    agent_account = accountDao.getAccountByAgentId(agent.getAgentId());
                    double newCustomerBalance = customer_account.getBalance() + requestItem.amount;
                    double newAgentBalance = agent_account.getBalance() - requestItem.amount;

                    accountDao.updateBalance(customer_account.getAccountId(), newCustomerBalance);
                    accountDao.updateBalance(agent_account.getAccountId(), newAgentBalance);

                    this.transactionDao.createTransaction(customer.getCustomerId(), agent.getAgentId(),
                            requestItem.request_type, requestItem.amount, "SUCCESS", requestItem.description);
                } catch (Exception e) {
                    System.err.println("error: " + e.getMessage());
                }
            } else if (requestItem.request_type.equals("WITHDRAW")) {
                String sql = "UPDATE money_requests SET status = ? WHERE request_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, status);
                    stmt.setLong(2, request_id);
                    stmt.executeUpdate();

                    customer_account = accountDao.getAccountByCustomerId(customer.getCustomerId());
                    agent_account = accountDao.getAccountByAgentId(agent.getAgentId());
                    double newCustomerBalance = customer_account.getBalance() - requestItem.amount;
                    double newAgentBalance = agent_account.getBalance() + requestItem.amount;

                    accountDao.updateBalance(customer_account.getAccountId(), newCustomerBalance);
                    accountDao.updateBalance(agent_account.getAccountId(), newAgentBalance);

                    this.transactionDao.createTransaction(customer.getCustomerId(), agent.getAgentId(),
                            requestItem.request_type, requestItem.amount, "SUCCESS", requestItem.description);
                } catch (Exception e) {
                    System.err.println("error: " + e.getMessage());
                }
            }


        } else if (status.equals("DENY")) {
            RequestItem requestItem = getRequestById(request_id);
            Customer customer = new CustomerDao(this.conn).findById(requestItem.request_from);
            Agent agent = new AgentDao(this.conn).findById(requestItem.agent_id);

            String sql = "UPDATE money_requests SET status = ? WHERE request_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setLong(2, request_id);
                stmt.executeUpdate();
                this.transactionDao.createTransaction(customer.getCustomerId(), agent.getAgentId(), requestItem.request_type,
                        requestItem.amount, "FAILED", requestItem.description);
            } catch (Exception e) {
                System.err.println("error: " + e.getMessage());
            }
        }
    }

    public List<RequestItem> getMoneyRequests(String email) {
        AgentDao agentDao = new AgentDao(DBConnection.getConnection());
        Agent agent = agentDao.findByEmail(email);

        List<RequestItem> list = new ArrayList<>();

        // SQL with LEFT JOIN to get Customer names
        String sql = "SELECT r.*, c.first_name, c.last_name " +
                "FROM money_requests r " +
                "LEFT JOIN customer c ON r.request_from = c.customer_id " +
                "WHERE r.agent_id = ? " +
                "ORDER BY r.requested_at DESC";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Use PreparedStatement properly to prevent SQL Injection
            stmt.setLong(1, agent.getAgentId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long request_id = rs.getLong("request_id");
                    long request_from = rs.getLong("request_from");
                    long agent_id = rs.getLong("agent_id");
                    String request_type = rs.getString("request_type");
                    double amount = rs.getDouble("amount");
                    String status = rs.getString("status");
                    String description = rs.getString("description");

                    LocalDateTime requestedAt = rs.getObject("requested_at", LocalDateTime.class);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");

                    String formattedDate = requestedAt.format(formatter);

                    // Get Customer Name from the JOIN
                    String customerName = rs.getString("first_name") + " " + rs.getString("last_name");

                    // Update your constructor call (see step 2 below)
                    list.add(new RequestItem(request_id, request_from, agent_id, request_type,
                            amount, status, description, formattedDate, customerName));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public RequestItem getRequestById(long requestId) {
        // SQL with LEFT JOIN to get the customer's name for this specific ID
        String sql = "SELECT r.*, c.first_name, c.last_name " +
                "FROM money_requests r " +
                "LEFT JOIN customer c ON r.request_from = c.customer_id " +
                "WHERE r.request_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, requestId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Map the row data to variables
                    long id = rs.getLong("request_id");
                    long from = rs.getLong("request_from");
                    long agentId = rs.getLong("agent_id");
                    String type = rs.getString("request_type");
                    double amt = rs.getDouble("amount");
                    String status = rs.getString("status");
                    String description = rs.getString("description");
                    String ts = rs.getString("requested_at");
                    String name = rs.getString("first_name") + " " + rs.getString("last_name");

                    // Return a new RequestItem object
                    return new RequestItem(id, from, agentId, type, amt, status, description, ts, name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching request by ID: " + e.getMessage());
        }
        return null; // Return null if the request was not found
    }

    public static class RequestItem {
        public long request_id; // Added ID
        public long request_from;
        long agent_id;
        public String request_type;
        public double amount;
        public String status;
        public String description;
        public String requested_at;
        public String customerName;

        public RequestItem(long request_id, long request_from, long agent_id, String request_type, double amount,
                           String status, String description, String requested_at, String customerName) {
            this.request_id = request_id;
            this.request_from = request_from;
            this.agent_id = agent_id;
            this.request_type = request_type;
            this.amount = amount;
            this.status = status;
            this.description = description;
            this.customerName = customerName;
            this.requested_at = requested_at;
        }
    }
}
