

package bankify.admin;

import bankify.Agent;
import bankify.dao.AdminDao;
import bankify.dao.AgentDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class AdminDashboard extends JFrame {

    // Stats
    private JLabel lblTotalUsers, lblActiveAccounts, lblTotalBalance;
    private static Agent agent;
    private static AgentDao agentDao;
    private static AdminDao adminDao;
    private static Connection conn;
    private static int totalUsers;
    private static int activeAccounts;
    private static String totalBalance;

    public AdminDashboard(Agent ag, AgentDao agd, Connection connection) {
        agent = ag;
        agentDao = agd;
        conn = connection;
        adminDao = new AdminDao(conn);
        totalUsers = adminDao.getUsersCount();
        activeAccounts = adminDao.getActiveUsers();
        totalBalance = adminDao.getTotalBalanceOfAllCustomers();

        setTitle("Bankify - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // Sidebar
        AdminSidebar sidebar = new AdminSidebar(this, "Dashboard");
        sidebar.setBackground(new Color(255, 255, 255));

        // Content Panel (using same style as HomePage)
        JPanel contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30, 127, 179));
        contentPanel.setLayout(null);

        // Admin Profile Panel
        RoundedPanel adminPanel = new RoundedPanel();
        adminPanel.setBounds(70, 70, 220, 80);
        adminPanel.setBackground(new Color(0, 191, 255));
        adminPanel.setLayout(null);

        JLabel adminIcon = new JLabel();
        adminIcon.setBounds(20, 11, 60, 60);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Resources/admin_profile.png"));
            if (icon.getImage() != null) {
                Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                adminIcon.setIcon(new ImageIcon(img));
            } else {
                adminIcon.setIcon(null);
                adminIcon.setText("");
            }
        } catch (Exception e) {
            adminIcon.setIcon(null);
            adminIcon.setText("");
        }
        adminPanel.add(adminIcon);

        JLabel adminNameLabel = new JLabel("Admin");
        adminNameLabel.setBounds(90, 22, 140, 35);
        adminNameLabel.setForeground(Color.WHITE);
        adminNameLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        adminPanel.add(adminNameLabel);

        // Dashboard Stats Cards
        createStatsCards(contentPanel);

        // Quick Actions Panel
        createQuickActions(contentPanel);

        contentPanel.add(adminPanel);

        return contentPanel;
    }

    private RoundedPanel createStatCard(String title, String value, Color color) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new GridBagLayout());
        card.setBackground(color);
        card.setPreferredSize(new Dimension(210, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 36));

        gbc.gridy = 0;
        gbc.weighty = 0.3;
        card.add(titleLabel, gbc);
        gbc.insets = new Insets(0, 5, 10, 5);
        gbc.gridy = 1;
        gbc.weighty = 0.7;
        card.add(valueLabel, gbc);

        return card;
    }

    private void createStatsCards(JPanel contentPanel) {
        // Stats Panel Container
        JPanel statsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER,60, 0));
        statsContainer.setBounds(-15, 230, 930, 150);
        statsContainer.setOpaque(false);



        // Create cards
        RoundedPanel usersCard = createStatCard("Total Users", String.valueOf(totalUsers), new Color(50,205,50));
        RoundedPanel accountsCard = createStatCard("Active Accounts", String.valueOf(activeAccounts), new Color(255, 20, 147));
        RoundedPanel balanceCard = createStatCard("Total Balance", String.valueOf(totalBalance), new Color(142, 68, 173));

        statsContainer.add(usersCard);
        statsContainer.add(accountsCard);
        statsContainer.add(balanceCard);

        contentPanel.add(statsContainer);
    }

    private void createQuickActions(JPanel contentPanel) {

        // User Management
        JLabel usersIcon = new JLabel();
        usersIcon.setBounds(170, 410, 85, 85);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Resources/my_profile.png"));
            if (icon.getImage() != null) {
                Image img = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                usersIcon.setIcon(new ImageIcon(img));
            } else {
                usersIcon.setIcon(null);
                usersIcon.setText("");
            }
        } catch (Exception e) {
            usersIcon.setIcon(null);
            usersIcon.setText("");
        }
        contentPanel.add(usersIcon);

        JButton usersButton = new RoundedButton2("Users", new Color(255, 20, 147));
        usersButton.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        usersButton.setBounds(135, 530, 150, 57);

        // Add ActionListener for Users button
        usersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToPage("Users");
            }
        });

        contentPanel.add(usersButton);

        // Account Management
        JLabel accountsIcon = new JLabel();
        accountsIcon.setBounds(410, 410, 85, 85);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Resources/account_type.png"));
            if (icon.getImage() != null) {
                Image img = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                accountsIcon.setIcon(new ImageIcon(img));
            } else {
                accountsIcon.setIcon(null);
                accountsIcon.setText("");
            }
        } catch (Exception e) {
            accountsIcon.setIcon(null);
            accountsIcon.setText("");
        }
        contentPanel.add(accountsIcon);

        JButton accountsButton = new RoundedButton2("Accounts", new Color(39, 174, 96));
        accountsButton.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        accountsButton.setBounds(375, 530, 150, 57);

        // Add ActionListener for Accounts button
        accountsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToPage("Accounts");
            }
        });

        contentPanel.add(accountsButton);

        // Transactions
        JLabel txnIcon = new JLabel();
        txnIcon.setBounds(650, 410, 85, 85);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Resources/transactions.png"));
            if (icon.getImage() != null) {
                Image img = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                txnIcon.setIcon(new ImageIcon(img));
            } else {
                txnIcon.setIcon(null);
                txnIcon.setText("");
            }
        } catch (Exception e) {
            txnIcon.setIcon(null);
            txnIcon.setText("");
        }
        contentPanel.add(txnIcon);

        JButton txnButton = new RoundedButton2("Transactions", new Color(142, 68, 173));
        txnButton.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        txnButton.setBounds(615, 530, 150, 57);

        // Add ActionListener for Transactions button
        txnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToPage("Transactions");
            }
        });

        contentPanel.add(txnButton);
    }

    private void navigateToPage(String pageName) {
        // Check if already on this page
        if (pageName.equals("Dashboard")) {
            // Already on dashboard, do nothing
            return;
        }

        JFrame newPage = null;

        switch(pageName) {
            case "Users":
                newPage = new AdminUsersPage(conn);
                break;
            case "Accounts":
                 newPage = new AdminAccountsPage();
               break;
            case "Transactions":
                newPage = new AdminTransactionsPage(); // FIXED: Removed message box
                break;

        }

        if (newPage != null) {
            // Create new sidebar with updated active page
            AdminSidebar newSidebar = new AdminSidebar(newPage, pageName);

            // Add sidebar to the new page
            if (newPage.getContentPane() instanceof JPanel) {
                JPanel contentPane = (JPanel) newPage.getContentPane();
                contentPane.add(newSidebar, BorderLayout.WEST);
            }

            newPage.setSize(1200, 800);
            newPage.setLocationRelativeTo(null);
            newPage.setVisible(true);
            this.dispose();
        }
    }

    // RoundedPanel class
    private class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 80, 80);
            g2.dispose();
        }
    }

    // RoundedButton2 class
    private class RoundedButton2 extends JButton {
        private Color currentColor;
        public RoundedButton2(String text, Color baseColor) {
            super(text);
            this.currentColor = baseColor;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard frame = new AdminDashboard(agent, agentDao, conn);
            frame.setVisible(true);
        });
    }
}