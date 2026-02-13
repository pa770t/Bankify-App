package bankify;

import bankify.dao.AgentDao;
import bankify.service.EmailService;
import bankify.service.PageGuardService;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import bankify.dao.MoneyRequestDao;
import bankify.dao.MoneyRequestDao.RequestItem;

public class AgentRequestListPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private List<RequestItem> requestList;
    private JPanel listContainer;
    private JButton btnSort;
    private static Agent agent;
    private static AgentDao agentDao;
    private static Connection conn;
    private static MoneyRequestDao moneyRequestDao;

    public AgentRequestListPage(Agent agent, AgentDao agentDao, Connection connection) {
        if (agent == null) {
            PageGuardService.checkSession(this, agent);
            return;
        }
        this.agent = agent;
        this.agentDao = agentDao;
        conn = connection;
        moneyRequestDao = new MoneyRequestDao(conn);

        setTitle("Bankify - Request List");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initData();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 127, 179));

        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel with Scroll Pane
        listContainer = new JPanel();
        listContainer.setBackground(new Color(30, 127, 179));
        listContainer.setLayout(null);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(30, 127, 179));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);


        refreshList();
    }

    private void initData() {
        requestList = moneyRequestDao.getMoneyRequests(agent.getEmail());
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 127, 179));
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1200, 190));


        // --- 1. Agent Pill (Centered Top) ---
        int pillWidth = 220;
        int pillHeight = 60;
        int pillX = (1200 - pillWidth) / 2;
        int pillY = 50; // Moved up slightly to make room

        JPanel pillPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 191, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), pillHeight, pillHeight);
                g2.dispose();
            }
        };
        pillPanel.setBounds(pillX, pillY, pillWidth, pillHeight);
        pillPanel.setOpaque(false);
        pillPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));

        JLabel lblAgentIcon = createScaledImageLabel("/Resources/agent.png", 35, 35);
        pillPanel.add(lblAgentIcon);

        JLabel lblAgentText = new JLabel("Agent");
        lblAgentText.setForeground(Color.WHITE);
        lblAgentText.setFont(new Font("Tw Cen MT", Font.BOLD, 28));
        pillPanel.add(lblAgentText);
        panel.add(pillPanel);

        // --- 2. Button Row (Under the Pill) ---
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonRow.setBounds(0, 125, 1200, 50); // Spans full width, positioned below pill
        buttonRow.setOpaque(false);

        // A. Refresh Button
        JButton btnRefresh = new JButton("Refresh") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 191, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btnRefresh, "/Resources/refresh.png");
        btnRefresh.addActionListener(e -> refreshList());
        buttonRow.add(btnRefresh);

        // B. Sort Button
        btnSort = new JButton("Sort By") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 191, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btnSort, "/Resources/dropdown.png");

        JPopupMenu sortMenu = new JPopupMenu();
        JMenuItem itemDate = new JMenuItem("Sort by Date (Newest)");
        JMenuItem itemName = new JMenuItem("Sort by Name (A-Z)");
        itemDate.addActionListener(e -> sortByDate());
        itemName.addActionListener(e -> sortByName());
        sortMenu.add(itemDate);
        sortMenu.add(itemName);

        btnSort.addActionListener(e -> sortMenu.show(btnSort, 0, btnSort.getHeight()));
        buttonRow.add(btnSort);

        // C. Change Password Button (Red Background)
        JButton btnChangePass = new JButton("Change Password") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 20, 60)); // RED COLOR
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        // No icon for this one, just text style
        btnChangePass.setPreferredSize(new Dimension(170, 45));
        btnChangePass.setForeground(Color.WHITE);
        btnChangePass.setFont(new Font("Tw Cen MT", Font.BOLD, 17));
        btnChangePass.setContentAreaFilled(false);
        btnChangePass.setBorderPainted(false);
        btnChangePass.setFocusPainted(false);
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnChangePass.addActionListener(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(this, agent, agentDao);
            dialog.setVisible(true);
        });
        buttonRow.add(btnChangePass);

        panel.add(buttonRow);



        return panel;
    }

    // Helper method to reduce code duplication for Refresh/Sort buttons
    private void styleButton(JButton btn, String iconPath) {
        try {
            URL iconURL = getClass().getResource(iconPath);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(iconURL).getImage()
                        .getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                btn.setIcon(icon);
                btn.setIconTextGap(10);
            }
        } catch (Exception e) {}
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Tw Cen MT", Font.BOLD, 17));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private void sortByName() {
        if (requestList != null) {
            // Sort the existing list in memory
            Collections.sort(requestList, Comparator.comparing(item -> item.customerName));
            btnSort.setText("By Name");
            refreshUiList(); // Only redraw the UI, do NOT fetch from DB
        }
    }

    private void sortByDate() {
        if (requestList != null) {
            // Sort by requested_at descending (newest first)
            Collections.sort(requestList, (o1, o2) -> o2.requested_at.compareTo(o1.requested_at));
            btnSort.setText("By Date");
            refreshUiList(); // Only redraw the UI
        }
    }


    // Use this when you want to get fresh data from the DB (e.g. Refresh button)
    private void refreshList() {
        requestList = moneyRequestDao.getMoneyRequests(agent.getEmail());
        // Default sort is by date
        Collections.sort(requestList, (o1, o2) -> o2.requested_at.compareTo(o1.requested_at));
        refreshUiList();
    }

    // Use this for sorting or just redrawing the panels
    private void refreshUiList() {
        listContainer.removeAll();

        int startY = 20;
        int itemHeight = 90;
        int spacing = 110;

        for (RequestItem item : requestList) {
            JPanel itemPanel = createRequestItem(
                    item.customerName,
                    item.requested_at,
                    item.amount,
                    item.request_type,
                    600,
                    itemHeight
            );
            itemPanel.setBounds(300, startY, 600, itemHeight);

            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose();
                    new depositAgent(item, agent, agentDao, conn).setVisible(true);
                }
            });
            listContainer.add(itemPanel);
            startY += spacing;
        }

        // Set preferred size for scroll pane
        int totalHeight = startY + 50;
        listContainer.setPreferredSize(new Dimension(1150, totalHeight));
        listContainer.setMinimumSize(new Dimension(1150, totalHeight));

        listContainer.revalidate();
        listContainer.repaint();
    }


    private JPanel createRequestItem(String name, String date, double amount, String requestType, int width, int height) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);

                int amPillW = 140;
                int amPillH = 40;
                int amX = getWidth() - amPillW - 30;
                int amY = (getHeight() - amPillH) / 2;
                if (requestType.equals("DEPOSIT")) {
                    g2.setColor(new Color(201, 38, 38)); // Red Color
                } else if (requestType.equals("WITHDRAW")) {
                    g2.setColor(new Color(50, 205, 50)); // Green Color
                }
                g2.fillRoundRect(amX, amY, amPillW, amPillH, amPillH, amPillH);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(null);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = createScaledImageLabel("/Resources/my_profile.png", 50, 50);
        lblIcon.setBounds(20, 20, 50, 50);
        p.add(lblIcon);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        lblName.setBounds(90, 15, 300, 30);
        p.add(lblName);

        JLabel lblDate = new JLabel(String.valueOf(date));
        lblDate.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        lblDate.setForeground(Color.GRAY);
        lblDate.setBounds(90, 48, 300, 25);
        p.add(lblDate);

        JLabel lblAmount = new JLabel(String.valueOf(amount), SwingConstants.CENTER);
        lblAmount.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setBounds(600 - 140 - 30, (90 - 40) / 2, 140, 40);
        p.add(lblAmount);

        return p;
    }

    private JLabel createScaledImageLabel(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) return new JLabel(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        return new JLabel();
    }

    public static void launch(Agent agent, AgentDao agentDao) {
        if (agent == null) {
            new AgentLogin().setVisible(true);
        } else {
            new AgentRequestListPage(agent, agentDao, conn).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> AgentRequestListPage.launch(agent, agentDao));
    }

    // --- CHANGE PASSWORD DIALOG ---
    class ChangePasswordDialog extends JDialog {
        private JPasswordField currentPassField, newPassField, confirmPassField;
        private JLabel errCurrent, errNew, errConfirm;

        public ChangePasswordDialog(JFrame parent, Agent agent, AgentDao agentDao) {
            super(parent, "Change Password", true);
            // Increased size to accommodate bigger fields
            setSize(600, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 5, 8, 5);
            gbc.ipady = 10; // Make input fields taller

            // Bigger Fonts
            Font labelFont = new Font("Tw Cen MT", Font.BOLD, 16);
            Font fieldFont = new Font("Tw Cen MT", Font.PLAIN, 16);

            // Row 1: Current Password
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel l1 = new JLabel("Current Password:"); l1.setFont(labelFont);
            formPanel.add(l1, gbc);

            gbc.gridx = 1;
            currentPassField = new JPasswordField(25); // Wider fields (25 columns)
            currentPassField.setFont(fieldFont);
            formPanel.add(currentPassField, gbc);

            gbc.gridx = 1; gbc.gridy = 1; gbc.ipady = 0;
            errCurrent = new JLabel(); formPanel.add(styleErrorLabel(errCurrent), gbc);
            gbc.ipady = 10; // Reset padding for next field

            // Row 2: New Password
            gbc.gridx = 0; gbc.gridy = 2;
            JLabel l2 = new JLabel("New Password:"); l2.setFont(labelFont);
            formPanel.add(l2, gbc);

            gbc.gridx = 1;
            newPassField = new JPasswordField(25);
            newPassField.setFont(fieldFont);
            formPanel.add(newPassField, gbc);

            gbc.gridx = 1; gbc.gridy = 3; gbc.ipady = 0;
            errNew = new JLabel(); formPanel.add(styleErrorLabel(errNew), gbc);
            gbc.ipady = 10;

            // Row 3: Confirm Password
            gbc.gridx = 0; gbc.gridy = 4;
            JLabel l3 = new JLabel("Confirm Password:"); l3.setFont(labelFont);
            formPanel.add(l3, gbc);

            gbc.gridx = 1;
            confirmPassField = new JPasswordField(25);
            confirmPassField.setFont(fieldFont);
            formPanel.add(confirmPassField, gbc);

            gbc.gridx = 1; gbc.gridy = 5; gbc.ipady = 0;
            errConfirm = new JLabel(); formPanel.add(styleErrorLabel(errConfirm), gbc);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
            JButton saveButton = new JButton("Save Changes");
            saveButton.setFont(labelFont);
            saveButton.addActionListener(e -> validateAndSaveChanges());
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(labelFont);
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private JLabel styleErrorLabel(JLabel label) {
            label.setForeground(Color.RED);
            label.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            return label;
        }

        private void validateAndSaveChanges() {
            errCurrent.setText(" "); errNew.setText(" "); errConfirm.setText(" ");
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            boolean hasError = false;

            if (!agentDao.checkPassword(agent.getEmail(), currentPass)) {
                errCurrent.setText("Incorrect current password.");
                hasError = true;
            }

            String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            if (!Pattern.matches(passwordPattern, newPass)) {
                errNew.setText("<html>Password must be 8+ chars and include<br>uppercase, lowercase, number, & special char.</html>");
                hasError = true;
            }

            if (!newPass.equals(confirmPass)) {
                errConfirm.setText("New passwords do not match.");
                hasError = true;
            }

            if (hasError) return;

            String newHashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
            boolean success = agentDao.updatePassword(agent.getEmail(), newHashedPassword);

            if (success) {
                EmailService emailService = new EmailService();
                emailService.sendAgentPasswordChangeSuccessEmail(agent);
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}