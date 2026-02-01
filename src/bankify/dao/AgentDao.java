package bankify.dao;

import bankify.Account;
import bankify.Agent;
import bankify.Customer;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AgentDao {
    private Connection conn;

    public AgentDao(Connection conn) {
        this.conn = conn;
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
                        return agent;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    };

    public Agent findByEmail(String email) {
        String sql = "SELECT * FROM employee WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Agent a = new Agent();
                a.setAgentId(rs.getInt("employee_id"));
                a.setFullName(rs.getString("full_name"));
                a.setRole(rs.getString("role"));
                a.setGender(rs.getString("gender"));
                a.setEmail(rs.getString("email"));
                a.setPhoneNumber(rs.getString("phone_number"));
                a.setAddress(rs.getString("address"));
                a.setPassword(rs.getString("password"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Agent findById(long agentId) {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, agentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Agent a = new Agent();
                a.setAgentId(rs.getInt("employee_id"));
                a.setFullName(rs.getString("full_name"));
                a.setRole(rs.getString("role"));
                a.setGender(rs.getString("gender"));
                a.setEmail(rs.getString("email"));
                a.setPhoneNumber(rs.getString("phone_number"));
                a.setAddress(rs.getString("address"));
                a.setPassword(rs.getString("password"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
