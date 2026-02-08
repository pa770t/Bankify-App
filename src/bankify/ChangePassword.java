package bankify;

import bankify.dao.CustomerDao;
import bankify.service.AuthService;
import bankify.service.EmailService;
import bankify.service.PageGuardService;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;



import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.Connection;

public class ChangePassword extends JFrame {
    private static Customer customer;
    private static CustomerDao customerDao;
    private static Connection conn;

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;

    // Password fields
    private RoundedPasswordField pwtCurrent, pwtNew, pwtConfirm;
 // Error & Success Labels
    private JLabel errCurrent, errNew, errConfirm, successLabel;

    public ChangePassword(Customer customer, CustomerDao customerDao, Connection connection) {
        if (customer == null) {
            PageGuardService.checkSession(this, customer);
            return;
        }

        this.customer = customer;
        this.customerDao = customerDao;
        conn = connection;

        setTitle("Bankify - Change Password");
        // Screen Size ပြောင်းလဲခြင်း
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

     // Sidebar
        Sidebar sidebar = new Sidebar(this, "Settings", customer, customerDao, conn);

        // Content
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }


    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30,127,179));
        contentPanel.setLayout(null);

        
        // Header Panel - အချိုးအစား ချိန်ညှိခြင်း
        JPanel settingsHeaderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 70, 70);
                g2.dispose();
            }
            
        };
        
        settingsHeaderPanel.setLayout(null);
        settingsHeaderPanel.setBackground(new Color(0, 191, 255));
        settingsHeaderPanel.setBounds(330, 60, 260, 70);

        JLabel settingsIconLabel = new JLabel("");
        settingsIconLabel.setBounds(20, 15, 40, 40);
        URL settingsIconURL = getClass().getResource("/Resources/change_password.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            settingsIconLabel.setIcon(new ImageIcon(img));
        }
        settingsHeaderPanel.add(settingsIconLabel);

        JLabel settingsLabel = new JLabel("Change Password");
        settingsLabel.setBounds(65, 15, 180, 40);
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        // Header Font Size ကို 22 သို့ တိုးထားပါတယ်
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        settingsHeaderPanel.add(settingsLabel);

        contentPanel.add(settingsHeaderPanel);

       
        // Password Fields Positioning (1200x800 အတွက် Center ကျအောင် ချိန်ထားပါတယ်)
        pwtCurrent = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Current Password", pwtCurrent, 200));
        
        errCurrent = new JLabel("");
        errCurrent.setBounds(300, 282, 350, 20);
        errCurrent.setForeground(Color.RED);
        errCurrent.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        contentPanel.add(errCurrent);

        pwtNew = new RoundedPasswordField();
        contentPanel.add(createLabeledField("New Password", pwtNew, 310));
        
        errNew = new JLabel("");
        errNew.setBounds(300, 392, 350, 20);
        errNew.setForeground(Color.RED);
        errNew.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        contentPanel.add(errNew);


        pwtConfirm = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Confirm Password", pwtConfirm, 420));
        
        errConfirm = new JLabel("");
        errConfirm.setBounds(300, 501, 350, 20);
        errConfirm.setForeground(Color.RED);
        errConfirm.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        contentPanel.add(errConfirm);

        successLabel = new JLabel("");
        successLabel.setBounds(315, 512, 350, 25);
        successLabel.setForeground(new Color(50, 205, 50));
        successLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        contentPanel.add(successLabel);


     // Clear success label when user focuses on any password field
        FocusListener clearSuccess = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                successLabel.setText("");
            }
        };

        pwtCurrent.addFocusListener(clearSuccess);
        pwtNew.addFocusListener(clearSuccess);
        pwtConfirm.addFocusListener(clearSuccess);

     // Cancel Button
        RoundedCornerButton btnCancel = new RoundedCornerButton("Cancel", new Color(50, 205, 50));
        btnCancel.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnCancel.setBounds(310, 550, 120, 50);
        btnCancel.addActionListener(e -> {
            dispose();
            MainSettings.launch(customer, customerDao);
        });
        contentPanel.add(btnCancel);
        
        // OK Button
        RoundedCornerButton btnOK = new RoundedCornerButton("OK",new Color(220, 20, 60));
        btnOK.setBounds(500, 550, 120, 50);
        btnOK.setFont(new Font("Tw Cen MT", Font.BOLD, 18)); // Button font 18
        btnOK.addActionListener(e -> {
            errCurrent.setText("");
            errNew.setText("");
            errConfirm.setText("");
            successLabel.setText("");

            String current = new String(pwtCurrent.getPassword()).trim();
            String newPass = new String(pwtNew.getPassword()).trim();
            String confirm = new String(pwtConfirm.getPassword()).trim();

            boolean hasError = false;

            // 1️⃣ Check current password
            if (current.isEmpty()) {
                errCurrent.setText("Please enter current password!");
                hasError = true;
            } else if (!BCrypt.checkpw(current, customer.getPassword())) {
                // Use BCrypt check for hashed password
                errCurrent.setText("Current password is incorrect!");
                hasError = true;
            }

            // 2️⃣ Validate new password
            String strongPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

            if (newPass.isEmpty()) {
                errNew.setText("Please enter new password!");
                hasError = true;
            } else if (!newPass.matches(strongPattern)) {
                errNew.setText("8+ chars, upper, lower, number & special!");
                hasError = true;
            }

            // 3️⃣ Confirm password
            if (confirm.isEmpty()) {
                errConfirm.setText("Please confirm password!");
                hasError = true;
            } else if (!newPass.equals(confirm)) {
                errConfirm.setText("Passwords do not match!");
                hasError = true;
            }

            // 4️⃣ Save new password if no errors
            if (!hasError) {
                try {
                    AuthService auth = new AuthService(customerDao, conn); // calls CustomerDao.updatePassword
                    // internally
                    auth.changePassword(customer.getCustomerId(), current, newPass);

                    successLabel.setText("Password changed successfully!");
                    EmailService emailService = new EmailService();
                    emailService.sendPasswordChangeSuccessEmail(customer);
                    pwtCurrent.setText("");
                    pwtNew.setText("");
                    pwtConfirm.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errCurrent.setText("Error updating password!");
                }
            }
        });


        contentPanel.add(btnOK);

        return contentPanel;
    }

    private JPanel createLabeledField(String labelText, RoundedPasswordField field, int y) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Label Font 
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        label.setBounds(0, 0, 300, 25);
        panel.add(label);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 35, 330, 45); // Field ကို ပိုရှည်ပြီး ပိုမြင့်အောင်လုပ်ထားပါတယ်

        field.setBounds(0, 0, 330, 45);
        field.setEchoChar('•');
        field.setFont(new Font("Segoe UI", Font.PLAIN, 20)); 
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 50));
        layeredPane.add(field, JLayeredPane.DEFAULT_LAYER);

        JLabel eyeLabel = new JLabel();
        eyeLabel.setBounds(285, 10, 25, 25);
        eyeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        URL eyeURL = getClass().getResource("/Resources/eye.png");
        URL eyeClosedURL = getClass().getResource("/Resources/hide.png");

        if (eyeURL != null && eyeClosedURL != null) {
            ImageIcon openIcon = new ImageIcon(new ImageIcon(eyeURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            ImageIcon closedIcon = new ImageIcon(new ImageIcon(eyeClosedURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            eyeLabel.setIcon(closedIcon);

            eyeLabel.addMouseListener(new MouseAdapter() {
                boolean visible = false;
                @Override
                public void mouseClicked(MouseEvent e) {
                    visible = !visible;
                    field.setEchoChar(visible ? (char) 0 : '•');
                    eyeLabel.setIcon(visible ? openIcon : closedIcon);
                }
            });
        }
        layeredPane.add(eyeLabel, JLayeredPane.PALETTE_LAYER);
        panel.add(layeredPane);

        panel.setBounds(300, y, 330, 90); // Panel position ချိန်ညှိမှု
        return panel;
    }

    private class RoundedCornerButton extends JButton {
        private final Color baseColor;
        private Color currentColor;
        public RoundedCornerButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.currentColor = color;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = baseColor.darker(); repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = baseColor; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class RoundedPasswordField extends JPasswordField {
        private static final long serialVersionUID = 1L;
        public RoundedPasswordField() {
            setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,getHeight(),getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void launch(Customer customer, CustomerDao customerDao, Connection con) {
        if (customer == null) {
            new Login().setVisible(true);
        } else {
            new ChangePassword(customer, customerDao, con).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> ChangePassword.launch(customer, customerDao, conn));
    }
}
