package bankify.dao;

import bankify.Agent;
import bankify.Customer;
import bankify.admin.AdminAccountsPage;
import bankify.admin.AdminTransactionsPage;
import bankify.admin.AdminUsersPage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {
    private static Connection conn;
    private static AccountDao accountDao;

    public AdminDao(Connection connection) {
        conn = connection;
        accountDao = new AccountDao(conn);
    }

    public boolean createAdmin(String full_name, String role, String gender, String email, String phone_number,
                             String address, String password) {

        Agent agent = new Agent();

        agent.setFullName(full_name);
        agent.setRole(role);
        agent.setGender(gender);
        agent.setEmail(email);
        agent.setPhoneNumber(phone_number);
        agent.setAddress(address);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        agent.setPassword(hashedPassword);
        String sql = "INSERT INTO employee (full_name, role, gender, email, phone_number, address, password) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, agent.getFullName());
            stmt.setString(2, agent.getRole());
            stmt.setString(3, agent.getGender());
            stmt.setString(4, agent.getEmail());
            stmt.setString(5, agent.getPhoneNumber());
            stmt.setString(6, agent.getAddress());
            stmt.setString(7, agent.getPassword());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated keys (the ID)
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Update the agent object with the new ID from the DB
                        agent.setAgentId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    };

    public int getUsersCount() {
        // Assuming the table from your first message is named 'customers'
        String sql = "SELECT COUNT(*) FROM customer";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Return the count from the first column
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return 0 if there is an error or no users found
        return 0;
    }

    public int getActiveUsers() {
        // Assuming the table from your first message is named 'customers'
        String sql = "SELECT COUNT(*) FROM account WHERE status = 'ACTIVE' and account_type = 'USER'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Return the count from the first column
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return 0 if there is an error or no users found
        return 0;
    }

    public String getTotalBalanceOfAllCustomers() {
        String sql = "SELECT SUM(balance) FROM account WHERE account_type = 'USER'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Get the value from the first column as a double
                // Check for Billions
                if (rs.getDouble(1) >= 1_000_000_000) {
                    return String.format("%.1fB", rs.getDouble(1) / 1_000_000_000);
                }
                // Check for Millions
                else if (rs.getDouble(1) >= 1_000_000) {
                    return String.format("%.1fM", rs.getDouble(1) / 1_000_000);
                }
                // Check for Thousands
                else if (rs.getDouble(1) >= 1_000) {
                    return String.format("%.1fk", rs.getDouble(1) / 1_000);
                }
                // Less than 1000
                else {
                    return String.format("%.2f", rs.getDouble(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.valueOf(0);
    }

    public Customer createCustomerByAdmin(Customer customer) {
        String sql = "INSERT INTO customer (first_name, last_name, email, phone_number, address, password, " +
                "first_time_login) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getPassword());
            stmt.setBoolean(7, customer.isFirstTimeLogin());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated keys (the ID)
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Update the customer object with the new ID from the DB
                        customer.setCustomerId(generatedKeys.getInt(1));
                        return customer;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Agent createAgent(String full_name, String role, String gender, String email, String phone_number,
                             String address, String password) {

        Agent agent = new Agent();

        agent.setFullName(full_name);
        agent.setRole(role);
        agent.setGender(gender);
        agent.setEmail(email);
        agent.setPhoneNumber(phone_number);
        agent.setAddress(address);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        agent.setPassword(hashedPassword);
        String sql = "INSERT INTO employee (full_name, role, gender, email, phone_number, address, password) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, agent.getFullName());
            stmt.setString(2, agent.getRole());
            stmt.setString(3, agent.getGender());
            stmt.setString(4, agent.getEmail());
            stmt.setString(5, agent.getPhoneNumber());
            stmt.setString(6, agent.getAddress());
            stmt.setString(7, agent.getPassword());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated keys (the ID)
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Update the agent object with the new ID from the DB
                        agent.setAgentId(generatedKeys.getInt(1));
                        AccountDao accountDao = new AccountDao(conn);
                        accountDao.createAgentAccount(agent);
                        return agent;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    };

    public List<AdminUsersPage.User> getAllUsers() {
        List<AdminUsersPage.User> allUsers = new ArrayList<>();

        // --- UNIFIED SQL QUERY WITH UNION ALL AND ORDER BY ---
        // This query combines customers and employees into a single list
        // and sorts the entire result set by the creation date in descending order.
        String unifiedSql =
                "(SELECT " +
                        "    c.customer_id AS id, " +
                        "    CONCAT(c.first_name, ' ', c.last_name) AS name, " +
                        "    c.email, " +
                        "    c.phone_number, " +
                        "    c.address, " +
                        "    'N/A' AS gender, " +      // Customers don't have gender, so we use a placeholder
                        "    'Customer' AS role, " +  // This part of the query is for Customers
                        "    'Active' AS status, " +  // Assuming a default status
                        "    c.created_at " +
                        "FROM customer c) " +

                        "UNION ALL " + // Combines the two queries

                        "(SELECT " +
                        "    e.employee_id AS id, " +
                        "    e.full_name AS name, " +
                        "    e.email, " +
                        "    e.phone_number, " +
                        "    e.address, " +
                        "    e.gender, " +
                        "    e.role, " +              // Role comes from the database for employees
                        "    'Active' AS status, " +  // Assuming a default status
                        "    e.created_at " +
                        "FROM employee e) " +

                        "ORDER BY created_at DESC"; // Sorts the final combined list (newest first)

        try (PreparedStatement stmt = conn.prepareStatement(unifiedSql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Create a new UI User object for each row in the combined result
                AdminUsersPage.User user = new AdminUsersPage.User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setGender(rs.getString("gender"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setJoinDate(rs.getDate("created_at").toLocalDate().toString());

                allUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or handle with proper logging
        }

        return allUsers;
    }

    public boolean deleteCustomer(int customerId) {
        // 1. Define the SQL query
        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 2. Set the ID parameter
            stmt.setInt(1, customerId);

            // 3. Execute the delete
            int affectedRows = stmt.executeUpdate();

            // 4. Return true if a row was actually removed
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAgent(int agentId) {
        // 1. Define the SQL query (Targeting the 'employee' table)
        String sql = "DELETE FROM employee WHERE employee_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 2. Set the ID parameter
            stmt.setInt(1, agentId);

            // 3. Execute the delete
            int affectedRows = stmt.executeUpdate();

            // 4. Return true if a row was actually removed
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting agent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<AdminAccountsPage.Account> getAllAccounts() {
        List<AdminAccountsPage.Account> accounts = new ArrayList<>();

        // Query 1: Get Customer Accounts
        // We map 'Customer' to 'Normal User' to match your filter logic
        String customerSql =
                "SELECT " +
                        "   a.account_number, " +
                        "   CONCAT(c.first_name, ' ', c.last_name) AS holder_name, " +
                        "   c.email, " +
                        "   c.phone_number, " +
                        "   a.account_type, " +
                        "   a.balance, " +
                        "   a.status, " +
                        "   a.created_at, " +
                        "   'Normal User' AS user_role " +
                        "FROM account a " +
                        "JOIN customer c ON a.customer_id = c.customer_id";

        // Query 2: Get Agent Accounts (assuming linked to employee table)
        String agentSql =
                "SELECT " +
                        "   a.account_number, " +
                        "   e.full_name AS holder_name, " +
                        "   e.email, " +
                        "   e.phone_number, " +
                        "   a.account_type, " +
                        "   a.balance, " +
                        "   a.status, " +
                        "   a.created_at, " +
                        "   'Agent' AS user_role " +
                        "FROM account a " +
                        "JOIN employee e ON a.employee_id = e.employee_id";

        // Combine them
        String unionSql = "(" + customerSql + ") UNION ALL (" + agentSql + ") ORDER BY created_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(unionSql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AdminAccountsPage.Account acc = new AdminAccountsPage.Account();
                acc.setAccountNumber(rs.getString("account_number"));
                acc.setAccountHolder(rs.getString("holder_name"));
                acc.setEmail(rs.getString("email"));
                acc.setPhone(rs.getString("phone_number"));
                acc.setAccountType(rs.getString("account_type")); // e.g. Savings
                acc.setBalance(rs.getDouble("balance"));
                acc.setStatus(rs.getString("status"));
                acc.setCreatedDate(rs.getString("created_at"));
//                acc.setUserRole(rs.getString("user_role")); // "Normal User" or "Agent"

                accounts.add(acc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }

    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM account WHERE account_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<AdminTransactionsPage.Transaction> getAllTransactions() {
        List<AdminTransactionsPage.Transaction> transactions = new ArrayList<>();

        // This query joins transactions with accounts, and then with customers/employees
        // to find the name of the user associated with each transaction.
        String sql =
                "SELECT " +
                        "    t.transaction_id, " +
                        "    t.transaction_type, " +
                        "    t.amount, " +
                        "    t.status, " +
                        "    t.transaction_at, " +
                        "    COALESCE(c.customer_id, e.employee_id) AS user_id, " +
                        "    COALESCE(CONCAT(c.first_name, ' ', c.last_name), e.full_name) AS user_name " +
                        "FROM transactions t " +
                        "JOIN account a ON t.account_id = a.account_id " +
                        "LEFT JOIN customer c ON a.customer_id = c.customer_id " +
                        "LEFT JOIN employee e ON a.employee_id = e.employee_id " +
                        "ORDER BY t.transaction_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String transactionId = String.valueOf(rs.getLong("transaction_id"));
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");
                Date date = rs.getDate("transaction_at");

                String userId = String.valueOf(rs.getInt("user_id"));
                String userName = rs.getString("user_name");

                String fromUserId = "N/A";
                String fromUserName = "External";
                String toUserId = "N/A";
                String toUserName = "External";
                String uiType = "Unknown";

                // Logic to map the single transaction entry to a "From/To" model for the UI
                switch (type) {
                    case "DEPOSIT":
                        toUserId = userId;
                        toUserName = userName;
                        uiType = "Deposit";
                        break;
                    case "WITHDRAW":
                        fromUserId = userId;
                        fromUserName = userName;
                        uiType = "Withdrawal";
                        break;
                    case "SEND":
                        fromUserId = userId;
                        fromUserName = userName;
                        toUserName = "Recipient"; // We don't know the recipient from this single row
                        uiType = "Transfer";
                        break;
                    case "RECEIVE":
                        fromUserName = "Sender"; // We don't know the sender from this single row
                        toUserId = userId;
                        toUserName = userName;
                        uiType = "Transfer";
                        break;
                }

                // Create the Transaction object for the UI table
                transactions.add(new AdminTransactionsPage.Transaction(
                        transactionId,
                        fromUserId,
                        fromUserName,
                        toUserId,
                        toUserName,
                        uiType, // Use the mapped UI type
                        amount,
                        status,
                        date
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle with proper logging
        }

        return transactions;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET first_name = ?, last_name = ?, email = ?, phone_number = ?, address = ? WHERE customer_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setLong(6, customer.getCustomerId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Updating customer failed, no rows affected.");
            }
        }
    }

    public void updateAgent(Agent agent) throws SQLException {
        String sql = "UPDATE employee SET full_name = ?, email = ?, phone_number = ?, address = ?, role = ?, gender = ? WHERE employee_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, agent.getFullName());
            stmt.setString(2, agent.getEmail());
            stmt.setString(3, agent.getPhoneNumber());
            stmt.setString(4, agent.getAddress());
            stmt.setString(5, agent.getRole());   // 'STAFF' or 'SYSTEM'
            stmt.setString(6, agent.getGender()); // 'MALE', 'FEMALE', etc.
            stmt.setLong(7, agent.getAgentId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Updating agent failed, no rows affected.");
            }
        }
    }

    public boolean hasEmailForBoth(String email) {
        Customer cus = new CustomerDao(conn).findByEmail(email);
        AgentDao agentDao = new AgentDao(conn);
        Agent a = agentDao.findByEmail(email);

        if (cus != null) {
            return true;
        } else return a != null;
    }

    public boolean hasPhoneForBoth(String phone) {
        Customer cus = new CustomerDao(conn).findByPhoneNumber(phone);
        AgentDao agentDao = new AgentDao(conn);
        Agent a = agentDao.findByPhoneNumber(phone);

        if (cus != null) {
            return true;
        } else return a != null;
    }
}
