package bankify.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class AdminDeactivateAccount extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;
    private RoundedPasswordField pwtPassword;
    private JLabel errPassword;

    public AdminDeactivateAccount() {
        setTitle("Bankify Admin - Deactivate Account");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar - AdminSidebar ကို သုံးထားပါတယ်
        // "AdminDeactivate" လို့ ပေးထားလို့ Sidebar က Settings ကို နှိပ်ရင် Settings Page ကို ပြန်သွားမှာပါ
        AdminSidebar sidebar = new AdminSidebar(this, "AdminDeactivate");

        // Content
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30, 127, 179));
        contentPanel.setLayout(null);

        // --- Header Panel ---
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
        settingsHeaderPanel.setBounds(300, 60, 281, 70);

        JLabel settingsIconLabel = new JLabel("");
        settingsIconLabel.setBounds(20, 15, 40, 40);
        URL settingsIconURL = getClass().getResource("/Resources/deactivate_account.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            settingsIconLabel.setIcon(new ImageIcon(img));
        }
        settingsHeaderPanel.add(settingsIconLabel);

        JLabel settingsLabel = new JLabel("Deactivate Account");
        settingsLabel.setBounds(70, 15, 189, 40);
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        settingsHeaderPanel.add(settingsLabel);
        contentPanel.add(settingsHeaderPanel);

        // --- Question Label ---
        JLabel lblQuestion = new JLabel("Do you want to deactivate your account?");
        lblQuestion.setOpaque(true);
        lblQuestion.setBackground(new Color(165, 42, 42));
        lblQuestion.setForeground(Color.WHITE);
        lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        lblQuestion.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        lblQuestion.setBounds(200, 200, 500, 60);
        contentPanel.add(lblQuestion);

        // --- Password Field ---
        pwtPassword = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Password", pwtPassword, 320));

        errPassword = new JLabel("");
        errPassword.setBounds(290, 405, 350, 20);
        errPassword.setForeground(Color.RED);
        errPassword.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        contentPanel.add(errPassword);

        // --- Buttons ---
        RoundedCornerButton btnCancel = new RoundedCornerButton("Cancel", new Color(50, 205, 50));
        btnCancel.setBounds(300, 480, 120, 50);
        btnCancel.addActionListener(e -> {
            dispose();
            new AdminSettingsPage().setVisible(true); // Settings Page ကို ပြန်သွားရန်
        });
        contentPanel.add(btnCancel);

        RoundedCornerButton btnOK = new RoundedCornerButton("OK", new Color(220, 20, 60));
        btnOK.setBounds(480, 480, 120, 50);
        btnOK.addActionListener(e -> handleDeactivation());
        contentPanel.add(btnOK);

        return contentPanel;
    }

    private void handleDeactivation() {
        if (!validatePassword()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to deactivate your account?",
            "Confirm Deactivation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Your account has been deactivated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            // Logout logic သို့မဟုတ် Login သို့ပြန်သွားရန်
            // new bankify.login.Login().setVisible(true); 
        }
    }

    private JPanel createLabeledField(String labelText, RoundedPasswordField field, int y) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 35, 330, 45);

        field.setBounds(0, 0, 330, 45);
        field.setEchoChar('•');
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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

        JLabel lblPassword = new JLabel(labelText);
        lblPassword.setBounds(0, 0, 150, 30);
        lblPassword.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        lblPassword.setForeground(Color.WHITE);
        panel.add(lblPassword);

        panel.setBounds(285, y, 330, 90);
        return panel;
    }

    private boolean validatePassword() {
        String password = new String(pwtPassword.getPassword());
        if (password.isEmpty()) {
            errPassword.setText("Password is required!");
            return false;
        }
        if (!password.equals("Aung1234@@")) { // Dummy check
            errPassword.setText("Incorrect password!");
            return false;
        }
        errPassword.setText("");
        return true;
    }

    // --- Style Classes (တူညီအောင် ထည့်ပေးထားပါတယ်) ---
    private class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() { setBorder(BorderFactory.createEmptyBorder(5,15,5,15)); }
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

    private class RoundedCornerButton extends JButton {
        private final Color baseColor;
        private Color currentColor;
        public RoundedCornerButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.currentColor = color;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE); setFont(new Font("Tw Cen MT", Font.BOLD, 18));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDeactivateAccount().setVisible(true));
    }
}