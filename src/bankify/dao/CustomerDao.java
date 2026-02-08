package bankify.dao;

import bankify.Agent;
import bankify.Customer;

import java.sql.*;

public class CustomerDao {
    private Connection conn;

    public CustomerDao(Connection conn) {
        this.conn = conn;
    }

    // Register a new customer
    public Customer register(Customer customer) {
        String sql = "INSERT INTO customer (first_name, last_name, email, password, first_time_login) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPassword());
            stmt.setBoolean(5, customer.isFirstTimeLogin());
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

    // Find customer by email
    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customer WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPassword(rs.getString("password"));
                c.setPhoneNumber(rs.getString("phone_number")); // ✅ fixed column name
                c.setAddress(rs.getString("address"));
                if (rs.getDate("dob") != null) {
                    c.setDob(rs.getDate("dob").toLocalDate());
                }
                c.setFirstTimeLogin(rs.getBoolean("first_time_login"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasEmailForBoth(String email) {
        Customer cus = findByEmail(email);
        AgentDao agentDao = new AgentDao(this.conn);
        Agent a = agentDao.findByEmail(email);

        if (cus != null) {
            return true;
        } else return a != null;
    }
    
    public boolean updateProfile(Customer customer) {
        String sql = "UPDATE customer SET phone_number = ?, address = ?, dob = ?, first_time_login = ? WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getPhoneNumber()); // ✅ matches DB column
            stmt.setString(2, customer.getAddress());
            if (customer.getDob() != null) {
                stmt.setDate(3, Date.valueOf(customer.getDob()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            stmt.setBoolean(4, false); // once profile is updated, mark as not first-time
            stmt.setString(5, customer.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Customer findById(long customer_id) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, customer_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPassword(rs.getString("password"));
                c.setPhoneNumber(rs.getString("phone_number")); // ✅ fixed column name
                c.setAddress(rs.getString("address"));
                if (rs.getDate("dob") != null) {
                    c.setDob(rs.getDate("dob").toLocalDate());
                }
                c.setFirstTimeLogin(rs.getBoolean("first_time_login"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer findByPhonenumber(String phoneNumber) {
        String sql = "SELECT * FROM customer WHERE phone_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPassword(rs.getString("password"));
                c.setPhoneNumber(rs.getString("phone_number"));
                c.setAddress(rs.getString("address"));
                if (rs.getDate("dob") != null) {
                    c.setDob(rs.getDate("dob").toLocalDate());
                }
                c.setFirstTimeLogin(rs.getBoolean("first_time_login"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(Customer customer) {
        if (customer == null || customer.getPassword() == null || customer.getPassword().isEmpty()) {
            return false;
        }

        String sql = "UPDATE customer SET password = ? WHERE customer_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getPassword());
            pstmt.setLong(2, customer.getCustomerId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error while updating password: " + e.getMessage());
            return false;
        }
    }
}