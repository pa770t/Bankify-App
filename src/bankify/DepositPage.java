package bankify;

import bankify.dao.AccountDao;
import bankify.dao.CustomerDao;
import bankify.dao.MoneyRequestDao;
import bankify.service.PageGuardService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URL;
import java.sql.SQLException;

public class DepositPage extends JFrame {
    private static Customer customer;
    private static CustomerDao customerDao;

    private static final long serialVersionUID = 1L;
    private JLabel lblBalance;
    private JTextField txtAgent;
    private JTextField txtAmount;
    private JTextField txtDescription;
    private boolean balanceVisible = true;
    private Account account;
    private AccountDao accountDao;
    private MoneyRequestDao moneyRequestDao;


    public DepositPage(Customer customer, CustomerDao customerDao) {
        if (customer == null) {
            PageGuardService.checkSession(this, customer);
            return;
        }

        this.customer = customer ;
        this.customerDao = customerDao;
        this.accountDao = new AccountDao(DBConnection.getConnection());
        this.moneyRequestDao = new MoneyRequestDao(DBConnection.getConnection());
        try {
            this.account = accountDao.getAccountByCustomerId(customer.getCustomerId());
        } catch (Exception e) {
            System.err.println("Can't find the account: " + e.getMessage());
        }

        setTitle("Bankify - Deposit");
        // Screen size updated to 1200x800
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        Sidebar sidebar = new Sidebar(this, "Deposit", customer, customerDao);
        JPanel contentPanel = createDepositContent();

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createDepositContent() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 127, 179));
        panel.setLayout(null);

        // User Profile Section
        JPanel userPanel = createRoundedPanel(new Color(0, 191, 255));
        userPanel.setBounds(70,50, 220, 80);
        userPanel.setLayout(null);

        JLabel lblUserIcon = createScaledImageLabel("/Resources/my_profile.png", 35, 35);
        lblUserIcon.setBounds(12,14,55,55);
        userPanel.add(lblUserIcon);

        JLabel lblUserName = new JLabel(customer.getFirstName() + " " + customer.getLastName());
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Increased Font Size
        lblUserName.setBounds(70,22,140,35);
        userPanel.add(lblUserName);
        panel.add(userPanel);

        // Account Balance Section
        JPanel balancePanel = createRoundedPanel(new Color(0, 191, 255));
        balancePanel.setBounds(70,160,470, 80);
        balancePanel.setLayout(null);

        JLabel lblBalanceIcon = createScaledImageLabel("/Resources/Balance.png", 40, 40);
        lblBalanceIcon.setBounds(20, 20, 43, 43);
        balancePanel.add(lblBalanceIcon);

        lblBalance = new JLabel("Account Balance : " + String.format("%.2f", account.getBalance()) + " MMK");
        lblBalance.setForeground(Color.WHITE);
        lblBalance.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Increased Font Size
        lblBalance.setBounds(75, 22, 370, 35);
        balancePanel.add(lblBalance);

        JButton eyeButton = new JButton();
        eyeButton.setBounds(410, 22, 35, 35);
        eyeButton.setContentAreaFilled(false); eyeButton.setBorderPainted(false); eyeButton.setFocusPainted(false);
        eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        URL eyeOpenURL = getClass().getResource("/Resources/eye.png");
        URL eyeClosedURL = getClass().getResource("/Resources/hide.png");

        if (eyeOpenURL != null && eyeClosedURL != null) {
            ImageIcon eyeOpenIcon = new ImageIcon(new ImageIcon(eyeOpenURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            ImageIcon eyeClosedIcon = new ImageIcon(new ImageIcon(eyeClosedURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            eyeButton.setIcon(eyeOpenIcon);
            eyeButton.addActionListener(e -> {
                balanceVisible = !balanceVisible;
                eyeButton.setIcon(balanceVisible ? eyeOpenIcon : eyeClosedIcon);
                lblBalance.setText(balanceVisible ? "Account Balance : " + String.format("%.2f", account.getBalance()) + " MMK" :
                        "Account Balance : ****** MMK");
            });
        }
        balancePanel.add(eyeButton);
        panel.add(balancePanel);

        // Input Fields with Better Fonts
        JLabel lblAgent = new JLabel("Agent");
        lblAgent.setBounds(70, 265, 100, 30);
        lblAgent.setForeground(Color.WHITE);
        lblAgent.setFont(new Font("Tw Cen MT", Font.BOLD, 20)); // Increased Font Size
        panel.add(lblAgent);

        txtAgent = new JTextField();
        txtAgent.setBounds(70, 305, 600, 55);
        txtAgent.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        txtAgent.setForeground(Color.GRAY);
        txtAgent.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        txtAgent.setText("Enter ID");
        txtAgent.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { if (txtAgent.getText().equals("Enter ID")) { txtAgent.setText(""); txtAgent.setForeground(Color.BLACK); } }
            public void focusLost(FocusEvent e) { if (txtAgent.getText().isEmpty()) { txtAgent.setText("Enter ID"); txtAgent.setForeground(Color.GRAY); } }
        });
        panel.add(txtAgent);

        JLabel lblAmount = new JLabel("Amount");
        lblAmount.setBounds(70, 370, 100, 30);
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setFont(new Font("Tw Cen MT", Font.BOLD, 20)); // Increased Font Size
        panel.add(lblAmount);

        txtAmount = new JTextField();
        txtAmount.setBounds(70, 410, 600, 55);
        txtAmount.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        txtAmount.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        txtAmount.setText("0");
        txtAmount.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { if (txtAmount.getText().equals("0")) txtAmount.setText(""); }
            public void focusLost(FocusEvent e) { if (txtAmount.getText().isEmpty()) txtAmount.setText("0"); }
        });
        panel.add(txtAmount);

        JLabel lblDescription = new JLabel("Description");
        lblDescription.setBounds(70, 480, 120, 30);
        lblDescription.setForeground(Color.WHITE);
        lblDescription.setFont(new Font("Tw Cen MT", Font.BOLD, 20)); // Increased Font Size
        panel.add(lblDescription);

        txtDescription = new JTextField();
        txtDescription.setBounds(70, 520, 600, 55);
        txtDescription.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        txtDescription.setForeground(Color.GRAY);
        txtDescription.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        txtDescription.setText("Optional note");
        txtDescription.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (txtDescription.getText().equals("Optional note")) {
                    txtDescription.setText("");
                    txtDescription.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtDescription.getText().isEmpty()) {
                    txtDescription.setText("Optional note");
                    txtDescription.setForeground(Color.GRAY);
                }
            }
        });
        panel.add(txtDescription);

        // Deposit Button
        RoundedButton btnDeposit = new RoundedButton("Deposit");
        btnDeposit.setBounds(285, 605, 180, 60);
        btnDeposit.setBackground(new Color(50, 205, 50));
        btnDeposit.setForeground(Color.WHITE);
        btnDeposit.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Increased Font Size
        btnDeposit.addActionListener(e -> handleDeposit());
        panel.add(btnDeposit);

        return panel;
    }

    private void handleDeposit() {
        String agent = txtAgent.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String description = txtDescription.getText().trim();

        if (agent.isEmpty() || agent.equals("Enter ID")) {
            JOptionPane.showMessageDialog(this, "Please enter Agent ID.");
            txtAgent.requestFocus(); return;
        }
        if (amountStr.isEmpty() || amountStr.equals("0")) {
            JOptionPane.showMessageDialog(this, "Please enter amount.");
            txtAmount.requestFocus(); return;
        }

        if (description.equals("Optional note")) description = "";

        try {
            double depositAmount = Double.parseDouble(amountStr);
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) { JOptionPane.showMessageDialog(this, "Amount must be greater than 0."); return; }

            if (depositAmount > 0) {
                long agentid = 0;

                try {
                    agentid = Long.parseLong(agent);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "There's no Agent by this ID!");
                    txtAgent.requestFocus();
                    return;
                }

                if (accountDao.getAccountByAgentId(agentid) == null) {
                    JOptionPane.showMessageDialog(this, "There's no Agent by this ID!");
                    txtAgent.requestFocus();
                    return;
                }

                moneyRequestDao.createMoneyRequest(customer.getCustomerId(), agentid, "DEPOSIT", amount, description);

                JOptionPane.showMessageDialog(this, "Your Deposit request for " + amount + " is pending...");
                txtAgent.setText("Enter ID"); txtAgent.setForeground(Color.GRAY);
                txtAmount.setText("0");
                txtDescription.setText("Optional note"); txtDescription.setForeground(Color.GRAY);
            } else {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid number for amount!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private JPanel createRoundedPanel(Color color) {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 80, 80);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JLabel createScaledImageLabel(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(img));
            }
        } catch (Exception e) {}
        return new JLabel();
    }

    private class RoundedButton extends JButton {
        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor;
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        public void setHoverBackgroundColor(Color color) { this.hoverBackgroundColor = color; }
        public void setPressedBackgroundColor(Color color) { this.pressedBackgroundColor = color; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ButtonModel model = getModel();
            Color currentColor = getBackground();
            if (model.isPressed() && pressedBackgroundColor != null) currentColor = pressedBackgroundColor;
            else if (model.isRollover() && hoverBackgroundColor != null) currentColor = hoverBackgroundColor;
            else if (model.isRollover() || model.isPressed()) currentColor = getBackground().darker();
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void launch(Customer customer, CustomerDao customerDao) {
        if (customer == null) {
            new Login().setVisible(true);
        } else {
            new DepositPage(customer, customerDao).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> DepositPage.launch(customer, customerDao));
    }
}