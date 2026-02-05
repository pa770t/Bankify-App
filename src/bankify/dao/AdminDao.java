package bankify.dao;

import bankify.Agent;
import bankify.Customer;
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

}
