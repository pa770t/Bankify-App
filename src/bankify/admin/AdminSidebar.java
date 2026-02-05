package bankify.admin;

import bankify.Agent;
import bankify.dao.AgentDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.Connection;

public class AdminSidebar extends JPanel {
    private JFrame parentFrame;
    private String activePage;
    private RoundedButton[] menuButtons;
    private static Agent agent;
    private static AgentDao agentDao;
    private static Connection conn;
    
    // Constructor မှာ activePage ကို လက်ခံပါ
    public AdminSidebar(JFrame parentFrame, String activePage) {
        this.parentFrame = parentFrame;
        this.activePage = activePage;
        
        // Initialize UI components
        initializeUI();
    }
    
    private void initializeUI() {
        setPreferredSize(new Dimension(280, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.WHITE);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        URL logoURL = getClass().getResource("/Resources/bank_logo.jpg");
        if (logoURL != null) {
            ImageIcon logoIcon = new ImageIcon(logoURL);
            Image img = logoIcon.getImage().getScaledInstance(220, 150, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        }
        header.add(logoLabel);
        header.add(Box.createVerticalStrut(10));

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        menuButtons = new RoundedButton[6];
        String[] buttonNames = {"Dashboard", "Users", "Accounts", "Transactions", "Security", "Settings"};
        
        for (int i = 0; i < buttonNames.length; i++) {
            String iconPath = getIconPathForButton(buttonNames[i]);
            menuButtons[i] = createAdminMenuButton(buttonNames[i], iconPath);
            menuPanel.add(menuButtons[i]);
            if (i < buttonNames.length - 1) {
                menuPanel.add(Box.createVerticalStrut(15));
            }
        }

        add(header, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
        
        updateButtonColors(); // Initial button colors update
    }

    private String getIconPathForButton(String buttonName) {
        switch(buttonName) {
            case "Dashboard": return "/Resources/dashboard.png";
            case "Users": return "/Resources/my_profile.png";
            case "Accounts": return "/Resources/account_type.png";
            case "Transactions": return "/Resources/transactions.png";
            case "Security": return "/Resources/security.png";
            case "Settings": return "/Resources/settings.png";
            default: return "";
        }
    }

    private RoundedButton createAdminMenuButton(String text, String iconPath) {
        RoundedButton btn = new RoundedButton(text);
        URL iconURL = getClass().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        }
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 60));
        btn.setPreferredSize(new Dimension(240, 60));

        // Set colors based on active page
        if (text.equals(activePage)) {
            btn.setBackground(new Color(0, 191, 255)); // Active Color
        } else {
            btn.setBackground(new Color(30, 127, 179)); // Default Color
        }

        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(15);
        btn.setFocusPainted(false);

        // Action Listeners for navigation
        btn.addActionListener(e -> {
            if (text.equals("Dashboard")) {
                // Check if already on Dashboard
                if (!activePage.equals("Dashboard")) {
                    navigate(new AdminDashboard(agent, agentDao, conn));
                }
            } else if (text.equals("Users")) {
                // Check if already on Users page
                if (!activePage.equals("Users")) {
                    navigate(new AdminUsersPage(conn));
                }
            } else if (text.equals("Accounts")) {
                // Check if already on Accounts page
                if (!activePage.equals("Accounts")) {
                    navigate(new AdminAccountsPage());
                }
            } else if (text.equals("Transactions")) {
                // Check if already on Transactions page
                if (!activePage.equals("Transactions")) {
                    navigate(new AdminTransactionsPage());
                }
            } else if (text.equals("Security")) {
                // Check if already on Security page
                if (!activePage.equals("Security")) {
                    navigate(new AdminSecurityPage());
                }
            } else if (text.equals("Settings")) {
                // Check if already on Settings page
                if (!activePage.equals("Settings")) {
                    navigate(new AdminSettingsPage());
                }
            }
        });

        // Mouse hover effects
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(activePage)) {
                    btn.setBackground(new Color(20, 100, 150));
                }
            }
            
            public void mouseExited(MouseEvent e) {
                if (!text.equals(activePage)) {
                    btn.setBackground(new Color(30, 127, 179));
                } else {
                    btn.setBackground(new Color(0, 191, 255));
                }
            }
        });

        return btn;
    }

    private void navigate(JFrame page) {
        // Set active page based on the page being navigated to
        String newActivePage = "";
        if (page instanceof AdminDashboard) {
            newActivePage = "Dashboard";
        } else if (page instanceof AdminUsersPage) {
            newActivePage = "Users";
        } else if (page instanceof AdminTransactionsPage) {
            newActivePage = "Transactions";
        } else if (page instanceof AdminAccountsPage) {
            newActivePage = "Accounts";
        } else if (page instanceof AdminSecurityPage) {
            newActivePage = "Security";
        } else if (page instanceof AdminSettingsPage) {
            newActivePage = "Settings"; // FIXED: Changed from "Setting" to "Settings"
        }
      
        // Check if already on this page (redundant check - already checked in action listener)
        // But keep it for safety
        if (newActivePage.equals(activePage)) {
            return;
        }
         
        // Create new sidebar with updated active page
        AdminSidebar newSidebar = new AdminSidebar(page, newActivePage);
        
        // If your pages use BorderLayout, you can add the sidebar to WEST
        if (page.getContentPane() instanceof JPanel) {
            JPanel contentPane = (JPanel) page.getContentPane();
            contentPane.add(newSidebar, BorderLayout.WEST);
        }
        
        page.setSize(1200, 800);
        page.setLocationRelativeTo(null);
        page.setVisible(true);
        parentFrame.dispose();
    }

    private void updateButtonColors() {
        for (RoundedButton btn : menuButtons) {
            if (btn != null) {
                if (btn.getText().equals(activePage)) {
                    btn.setBackground(new Color(0, 191, 255));
                } else {
                    btn.setBackground(new Color(30, 127, 179));
                }
                btn.repaint();
            }
        }
    }

    // Getter method for parent frame if needed
    public JFrame getParentFrame() {
        return parentFrame;
    }

    // Getter for active page
    public String getActivePage() {
        return activePage;
    }

    // Setter for active page (for dynamic updates)
    public void setActivePage(String activePage) {
        this.activePage = activePage;
        updateButtonColors();
    }

    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false); 
            setBorderPainted(false); 
            setFocusPainted(false); 
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
