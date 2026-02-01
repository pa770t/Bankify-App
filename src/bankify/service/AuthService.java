package bankify.service;

import bankify.Account;
import bankify.Agent;
import bankify.Customer;
import bankify.DBConnection;
import bankify.dao.AccountDao;
import bankify.dao.AgentDao;
import bankify.dao.CustomerDao;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {
    private CustomerDao customerDao;

    public AuthService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    // Register a new customer
    public Customer register(Customer customer) {
        if (customer == null ||
            customer.getFirstName() == null || customer.getFirstName().isEmpty() ||
            customer.getLastName() == null || customer.getLastName().isEmpty() ||
            customer.getEmail() == null || customer.getEmail().isEmpty() ||
            customer.getPassword() == null || customer.getPassword().isEmpty()) {
            return null;
        }

        // hash the password
        String hashedPassword = BCrypt.hashpw(customer.getPassword(), BCrypt.gensalt(12));

        customer.setPassword(hashedPassword);

        // Always mark new users as first-time login
        customer.setFirstTimeLogin(true);

        return customerDao.register(customer);
    }

    // Authenticate and return the Customer object
    public Customer authenticate(String email, String password) throws SQLException {
        if (email == null || password == null ||
            email.isEmpty() || password.isEmpty()) {
            return null;
        } else {
            Customer customer = customerDao.findByEmail(email);
            if (customer == null) {return null;}
            // Check if the plain text matches the hashed version
            if (BCrypt.checkpw(password, customer.getPassword())) {
                // Activate the customer if the customer's account is CLOSED
                AccountDao accountDao = new AccountDao(DBConnection.getConnection());
                Account account = accountDao.getAccountByNumber(customer.getPhoneNumber());
                if (account != null && account.getStatus().equals("CLOSED")) {
                    accountDao.activateAccount(account.getAccountId());
                }
                return customer;
            }
        }
        return null;
    }

    public Agent authenticateAgent(String email, String password, AgentDao agentDao) {
        if (email == null || password == null ||
                email.isEmpty() || password.isEmpty()) {
            return null;
        } else {
            Agent agent = agentDao.findByEmail(email);
            if (agent == null) {return null;}
            // Check if the plain text matches the hashed version
            if (BCrypt.checkpw(password, agent.getPassword())) {
                return agent;
            }
        }
        return null;
    }

    public boolean changePassword(long customerId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            return false; // new password cannot be empty
        }

        // 1. Fetch the customer by ID
        Customer customer = customerDao.findById(customerId);
        if (customer == null) return false;

        // 2. Verify old password
        if (!BCrypt.checkpw(oldPassword, customer.getPassword())) {
            return false; // old password does not match
        }

        // 3. Hash the new password
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

        // 4. Update the password in the database
        customer.setPassword(hashedPassword);

        return customerDao.updatePassword(customer); // return true if updated successfully
    }

}