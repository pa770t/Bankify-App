package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainSettings extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;

    public MainSettings() {
        setTitle("Bankify - Settings");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        Sidebar sidebar = new Sidebar(this, "Settings");
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30,127,179));
        contentPanel.setLayout(null);
        
        JPanel settingsHeaderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
            }
        };
        settingsHeaderPanel.setLayout(null);
        settingsHeaderPanel.setBackground(new Color(0, 191, 255));
        settingsHeaderPanel.setBounds(335, 50, 230, 70);
        
        JLabel lblNewLabel = new JLabel("");
        URL settingsIconURL = getClass().getResource("/Resources/settings2.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image scaledImage = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            lblNewLabel.setIcon(new ImageIcon(scaledImage));
        }
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(25, 12, 60, 45);
        settingsHeaderPanel.add(lblNewLabel);
        
        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        settingsLabel.setBounds(95, 12, 120, 45);
        settingsHeaderPanel.add(settingsLabel);
        contentPanel.add(settingsHeaderPanel);
        
        // My Profile
        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setIcon(new ImageIcon(MainSettings.class.getResource("/Resources/my_profile.png")));
        lblNewLabel_1.setBounds(150, 220, 60, 60);
        contentPanel.add(lblNewLabel_1);
        
        JButton btnNewButton = new RoundedButton2("My Profile");
        btnNewButton.setBackground(new Color(0, 191, 255));
        btnNewButton.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnNewButton.setBounds(110, 290, 140, 60); 
        btnNewButton.addActionListener(e -> openMyProfilePage());
        contentPanel.add(btnNewButton);
        
        // Account Type
        JLabel lblNewLabel_2 = new JLabel("");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_2.setIcon(new ImageIcon(MainSettings.class.getResource("/Resources/account_type.png")));
        lblNewLabel_2.setBounds(415, 220, 60, 60);
        contentPanel.add(lblNewLabel_2);
        
        JButton btnNewButton_1 = new RoundedButton2("<html><center>Account<br>Type</center></html>");
        btnNewButton_1.setBackground(new Color(0, 191, 255));
        btnNewButton_1.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnNewButton_1.setBounds(375, 290, 140, 60);
        btnNewButton_1.addActionListener(e -> openAccountTypePage());
        contentPanel.add(btnNewButton_1);
        
        // Change Password
        JLabel lblNewLabel_3 = new JLabel("");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_3.setIcon(new ImageIcon(MainSettings.class.getResource("/Resources/change_password.png")));
        lblNewLabel_3.setBounds(680, 220, 60, 60);
        contentPanel.add(lblNewLabel_3);
        
        JButton btnNewButton_2 = new RoundedButton2("<html><center>Change<br>Password</center></html>");
        btnNewButton_2.setBackground(new Color(0, 191, 255));
        btnNewButton_2.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnNewButton_2.setBounds(640, 290, 140, 60);
        btnNewButton_2.addActionListener(e -> openChangePasswordPage());
        contentPanel.add(btnNewButton_2);
        
        // Deactivate Account
        JLabel lblNewLabel_4 = new JLabel("");
        lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_4.setIcon(new ImageIcon(MainSettings.class.getResource("/Resources/deactivate_account.png")));
        lblNewLabel_4.setBounds(280, 450, 60, 60);
        contentPanel.add(lblNewLabel_4);
        
        JButton btnNewButton_3 = new RoundedButton2("<html><center>Deactivate<br>Account</center></html>");
        btnNewButton_3.setBackground(new Color(0, 191, 255));
        btnNewButton_3.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnNewButton_3.setBounds(240, 520, 140, 60);
        btnNewButton_3.addActionListener(e -> openDeactivateAccountPage());
        contentPanel.add(btnNewButton_3);
        
        // Log Out
        JLabel lblNewLabel_5 = new JLabel("");
        lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_5.setIcon(new ImageIcon(MainSettings.class.getResource("/Resources/logout.png")));
        lblNewLabel_5.setBounds(550, 450, 60, 60);
        contentPanel.add(lblNewLabel_5);
        
        JButton btnNewButton_4 = new RoundedButton2("Log Out");
        btnNewButton_4.setBackground(new Color(0, 191, 255));
        btnNewButton_4.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnNewButton_4.setBounds(510, 520, 140, 60); 
        btnNewButton_4.addActionListener(e -> openLogoutPage());
        contentPanel.add(btnNewButton_4);
        
        return contentPanel;
    }

    // ===== Navigation Methods =====
    private void openMyProfilePage() {
        new MyProfile().setVisible(true);
        this.dispose();
    }

    private void openAccountTypePage() {
        new AccountType().setVisible(true);
        this.dispose();
    }

    private void openChangePasswordPage() {
        new ChangePassword().setVisible(true);
        this.dispose();
    }

    private void openDeactivateAccountPage() {
        new DeactivateAccount().setVisible(true);
        this.dispose();
    }
    
    private void openLogoutPage() {
        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().setVisible(true); 
        }
    }

    // ===== Custom Components =====
    private class RoundedButton2 extends JButton {
        private static final long serialVersionUID = 1L;
        private Color hoverColor = new Color(30, 150, 255);
        private Color pressedColor = new Color(20, 120, 200);
        private Color currentColor = new Color(0, 191, 255);
        public RoundedButton2(String text) {
            super(text);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { currentColor = hoverColor; repaint(); }
                @Override public void mouseExited(MouseEvent e) { currentColor = new Color(0, 191, 255); repaint(); }
                @Override public void mousePressed(MouseEvent e) { currentColor = pressedColor; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { 
                    currentColor = contains(e.getPoint()) ? hoverColor : new Color(0, 191, 255);
                    repaint(); 
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
        @Override public void setBackground(Color bg) { currentColor = bg; repaint(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainSettings().setVisible(true));
    }
}