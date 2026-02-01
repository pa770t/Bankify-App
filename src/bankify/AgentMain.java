package bankify;

import javax.swing.*;

public class AgentMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                AgentLogin agentLogin = new AgentLogin();
                agentLogin.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
