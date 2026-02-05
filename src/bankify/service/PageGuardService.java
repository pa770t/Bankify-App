package bankify.service;
import bankify.*;
import bankify.dao.CustomerDao;

import javax.swing.*;
import java.sql.Connection;

public class PageGuardService {
    public static void checkSession(JFrame currentFrame, Customer customer) {
        if (customer == null) {
            currentFrame.dispose();
            new Login().setVisible(true);
        }
    }

    public static void checkSession(JFrame currentFrame, Agent agent) {
        if (agent == null) {
            currentFrame.dispose();
            new AgentLogin().setVisible(true);
        }
    }

    public static void checkSession(TransactionsPage transactionsPage, Customer customer) {
        if (customer == null) {
            new Login().setVisible(true);
        }
    }
}
