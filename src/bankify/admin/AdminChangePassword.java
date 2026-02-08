package bankify.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.Connection;

public class AdminChangePassword extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;
    private RoundedPasswordField pwtCurrent, pwtNew, pwtConfirm;
    private JLabel errCurrent, errNew, errConfirm, successLabel;
    private static Connection conn;

    public AdminChangePassword(Connection connection) {
        conn = connection;

        setTitle("Bankify Admin - Change Password");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar - Admin Sidebar ကို သုံးထားပါတယ်
        // "AdminSettings" လို့ နာမည်ပေးထားခြင်းဖြင့် Sidebar က Settings ကိုနှိပ်ရင် AdminSettingsPage ကို ပြန်ရောက်ပါမယ်
        AdminSidebar sidebar = new AdminSidebar(this, "AdminChangePass", conn);

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
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        settingsHeaderPanel.add(settingsLabel);
        contentPanel.add(settingsHeaderPanel);

        // --- Fields & Error Labels ---
        pwtCurrent = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Current Password", pwtCurrent, 200));
        errCurrent = createErrorLabel(300, 282);
        contentPanel.add(errCurrent);

        pwtNew = new RoundedPasswordField();
        contentPanel.add(createLabeledField("New Password", pwtNew, 310));
        errNew = createErrorLabel(300, 392);
        contentPanel.add(errNew);

        pwtConfirm = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Confirm Password", pwtConfirm, 420));
        errConfirm = createErrorLabel(300, 501);
        contentPanel.add(errConfirm);

        successLabel = new JLabel("");
        successLabel.setBounds(315, 512, 350, 25);
        successLabel.setForeground(new Color(50, 205, 50));
        successLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        contentPanel.add(successLabel);

        // Clear success logic
        FocusListener clearSuccess = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { successLabel.setText(""); }
        };
        pwtCurrent.addFocusListener(clearSuccess);
        pwtNew.addFocusListener(clearSuccess);
        pwtConfirm.addFocusListener(clearSuccess);

        // --- Buttons ---
        RoundedCornerButton btnCancel = new RoundedCornerButton("Cancel", new Color(50, 205, 50));
        btnCancel.setBounds(310, 550, 120, 50);
        btnCancel.addActionListener(e -> {
            dispose();
            new AdminSettingsPage(conn).setVisible(true);
        });
        contentPanel.add(btnCancel);

        RoundedCornerButton btnOK = new RoundedCornerButton("OK", new Color(220, 20, 60));
        btnOK.setBounds(500, 550, 120, 50);
        btnOK.addActionListener(e -> handlePasswordChange());
        contentPanel.add(btnOK);

        return contentPanel;
    }

    private void handlePasswordChange() {
        errCurrent.setText("");
        errNew.setText("");
        errConfirm.setText("");
        successLabel.setText("");

        String current = new String(pwtCurrent.getPassword());
        String newPass = new String(pwtNew.getPassword());
        String confirm = new String(pwtConfirm.getPassword());

        boolean hasError = false;
        String savedPassword = "Aung1234@@"; // Dummy

        if (current.isEmpty()) {
            errCurrent.setText("Please enter current password!");
            hasError = true;
        } else if (!current.equals(savedPassword)) {
            errCurrent.setText("Current password is incorrect!");
            hasError = true;
        }

        String strongPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";
        if (newPass.isEmpty()) {
            errNew.setText("Please enter new password!");
            hasError = true;
        } else if (!newPass.matches(strongPattern)) {
            errNew.setText("8+ chars, upper, lower, number & special!");
            hasError = true;
        }

        if (confirm.isEmpty()) {
            errConfirm.setText("Please confirm password!");
            hasError = true;
        } else if (!newPass.equals(confirm)) {
            errConfirm.setText("Passwords do not match!");
            hasError = true;
        }

        if (!hasError) {
            successLabel.setText("Password changed successfully!");
            pwtCurrent.setText("");
            pwtNew.setText("");
            pwtConfirm.setText("");
        }
    }

    private JLabel createErrorLabel(int x, int y) {
        JLabel label = new JLabel("");
        label.setBounds(x, y, 350, 20);
        label.setForeground(Color.RED);
        label.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        return label;
    }

    private JPanel createLabeledField(String labelText, RoundedPasswordField field, int y) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        label.setBounds(0, 0, 300, 25);
        panel.add(label);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 35, 330, 45);

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

        panel.setBounds(300, y, 330, 90);
        return panel;
    }

    // --- Custom Inner Classes (Style ပုံစံတူ) ---
    private class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() { setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
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
        SwingUtilities.invokeLater(() -> new AdminChangePassword(conn).setVisible(true));
    }
}