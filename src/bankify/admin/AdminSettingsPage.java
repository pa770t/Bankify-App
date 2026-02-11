package bankify.admin;

import bankify.Agent;
import bankify.AgentLogin;
import bankify.dao.AgentDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;

public class AdminSettingsPage extends JFrame {

    // Main Color Definition
    private final Color PRIMARY_COLOR = new Color(30, 127, 179);
    private final Color PILL_COLOR = new Color(0, 191, 255);
    private static Agent agent;
    private static AgentDao agentDao;
    private static Connection conn;

    public AdminSettingsPage(Agent ag, AgentDao agd, Connection connection) {
        agent = ag;
        agentDao = agd;
        conn = connection;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Bankify Admin - Settings");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Add Sidebar
        AdminSidebar sidebar = new AdminSidebar(this, "Settings", agent, agentDao, conn);
        add(sidebar, BorderLayout.WEST);

        // 2. Add Main Content
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(PRIMARY_COLOR);
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // --- 1. Header (Pill Shape with Icon) ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(60, 0, 80, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        PillPanel headerPill = new PillPanel("Settings", "/Resources/settings2.png");
        contentPanel.add(headerPill, gbc);

        // --- 2. Menu Options Grid ---
        int vGap = 60; // Vertical gap between buttons

        // -- Change Password --
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, vGap, 0); // Bottom gap
        addMenuOption(contentPanel, gbc, "<html><center>Change<br>Password</center></html>", "/Resources/change_password.png", e -> openChangePasswordPage());

        // -- Logout --
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // No extra insets needed, perfectly centered
        addMenuOption(contentPanel, gbc, "Log Out", "/Resources/logout.png", e -> openLogoutPage());

        // --- 3. Pusher Component (Pushes everything up) ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        contentPanel.add(Box.createGlue(), gbc);

        return contentPanel;
    }

    // Helper to add Icon + Button pair
    private void addMenuOption(JPanel panel, GridBagConstraints gbc, String text, String iconPath, java.awt.event.ActionListener action) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setOpaque(false);

        // 1. Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        URL imgUrl = getClass().getResource(iconPath);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } else {
            iconLabel.setText("Icon Missing");
            iconLabel.setForeground(Color.WHITE);
        }

        // 2. Button
        RoundedButton2 btn = new RoundedButton2(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(160, 60));
        btn.setMaximumSize(new Dimension(160, 60));
        btn.addActionListener(action);

        // Add with spacing
        itemPanel.add(iconLabel);
        itemPanel.add(Box.createVerticalStrut(15));
        itemPanel.add(btn);

        panel.add(itemPanel, gbc);
    }

// --- Navigation Methods ---

    // 2. Change Password Page သို့သွားရန်
    private void openChangePasswordPage() {
        this.dispose();
        new bankify.admin.AdminChangePassword(agent, agentDao, conn).setVisible(true);

    }

    // 4. Logout ပြုလုပ်ရန်
    private void openLogoutPage() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            // Login Page သို့ ပြန်ပို့ရန် (Login class ရှိရာ path ကို ထည့်ပါ)
            new AgentLogin().setVisible(true);
            System.out.println("Logged out successfully.");
        }
    }
    // --- Custom Pill Panel with Icon ---
    private class PillPanel extends JPanel {
        private String text;
        private Image iconImage;

        public PillPanel(String text, String iconPath) {
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(280, 70)); // Size slightly larger for icon

            // Load Icon
            URL url = getClass().getResource(iconPath);
            if (url != null) {
                this.iconImage = new ImageIcon(url).getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Background Pill
            g2.setColor(PILL_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);

            // Draw Content (Icon + Text) Centered
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Tw Cen MT", Font.BOLD, 26));
            FontMetrics fm = g2.getFontMetrics();

            int iconSize = 40; // Icon width/height
            int gap = 15;      // Space between icon and text
            int textWidth = fm.stringWidth(text);

            // Calculate total width to center both
            int totalContentWidth = iconSize + gap + textWidth;
            int startX = (getWidth() - totalContentWidth) / 2;
            int centerY = getHeight() / 2;

            // Draw Icon
            if (iconImage != null) {
                g2.drawImage(iconImage, startX, centerY - (iconSize/2), iconSize, iconSize, this);
            }

            // Draw Text
            int textY = centerY + (fm.getAscent() / 2) - 2; // Adjust for baseline
            g2.drawString(text, startX + iconSize + gap, textY);
        }
    }

    // --- Rounded Button Class ---
    private class RoundedButton2 extends JButton {
        private static final long serialVersionUID = 1L;
        private Color hoverColor = new Color(30, 150, 255);
        private Color pressedColor = new Color(20, 120, 200);
        private Color currentColor = new Color(0, 191, 255);

        public RoundedButton2(String text) {
            super(text);
            setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
            setFont(new Font("Tw Cen MT", Font.BOLD, 18));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = hoverColor; repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = new Color(0, 191, 255); repaint(); }
                public void mousePressed(MouseEvent e) { currentColor = pressedColor; repaint(); }
                public void mouseReleased(MouseEvent e) { currentColor = hoverColor; repaint(); }
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminSettingsPage(agent, agentDao, conn).setVisible(true));
    }
}
