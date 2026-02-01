package bankify;

import javax.swing.*;

public class CustomerMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Login customerLogin = new Login();
                customerLogin.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
