package bankify;

import bankify.dao.AccountDao;
import bankify.dao.CustomerDao;
import bankify.dao.TransferDao;
import bankify.service.PageGuardService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.SQLException;

public class TransferPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JLabel lblBalance;
    private JTextField txtTo;
    private JTextField txtAmount;
    private JTextField txtDescription;
    private double balanceAmount;
    private static Customer customer;
    private static CustomerDao customerDao;
    private AccountDao accountDao;
    private boolean balanceVisible = true;

    public TransferPage(Customer customer, CustomerDao customerDao) {
        if (customer == null) {
            PageGuardService.checkSession(this, customer);
            return;
        }

        this.customer = customer ;
        this.customerDao = customerDao;
        this.accountDao = new AccountDao(DBConnection.getConnection());

        setTitle("Bankify - Transfer");
        // Screen size updated to 1200x800
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar(this, "Transfer", customer, customerDao);

        add(sidebar, BorderLayout.WEST);
        add(createTransferContent(), BorderLayout.CENTER);
    }

    // ================= Transfer Content =================
    private JPanel createTransferContent() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(30, 127, 179));

        // User Profile Section
        JPanel userPanel = createRoundedPanel(new Color(0, 191, 255)); 
        userPanel.setBounds(70,50,220,80); // Bounds adjusted for 1200x800
        userPanel.setLayout(null);

        JLabel lblUserIcon = createScaledImageLabel("/Resources/my_profile.png", 40, 40);
        lblUserIcon.setBounds(12, 14, 55, 55);
        userPanel.add(lblUserIcon);

        JLabel lblUserName = new JLabel(customer.getFirstName() + " " + customer.getLastName());
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Font size increased
        lblUserName.setBounds(70, 22, 140, 35);
        userPanel.add(lblUserName);
        panel.add(userPanel);

     // Phone Number Panel 
        JPanel phonePanel = createRoundedPanel(new Color(0, 191, 255));  
        phonePanel.setBounds(320, 50, 470, 80); // Adjusted position
        phonePanel.setBackground(new Color(0, 191, 255));
        phonePanel.setLayout(null);

        JLabel phoneIcon = new JLabel();
        phoneIcon.setBounds(20, 22, 95, 40);
        URL phoneIconURL = getClass().getResource("/Resources/phone.png");
        if (phoneIconURL != null) {
            Image img = new ImageIcon(phoneIconURL).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            phoneIcon.setIcon(new ImageIcon(img));
        } 
        phonePanel.add(phoneIcon);

        JLabel phoneLabel = new JLabel("Your Phone Number: +95" + customer.getPhoneNumber());
        phoneLabel.setBounds(70, 25, 395, 35);
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        phonePanel.add(phoneLabel);


        // Account Balance Section
        JPanel balancePanel = createRoundedPanel(new Color(0, 191, 255));
        balancePanel.setBounds(70,160,470, 80); // Size increased
        balancePanel.setLayout(null);

        JLabel lblBalanceIcon = createScaledImageLabel("/Resources/Balance.png", 40, 40);
        lblBalanceIcon.setBounds(20, 20, 43, 43);
        balancePanel.add(lblBalanceIcon);

        lblBalance = new JLabel("Account Balance : " + String.format("%.2f", balanceAmount) + " MMK");
        lblBalance.setForeground(Color.WHITE);
        lblBalance.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Font size increased
        lblBalance.setBounds(75, 22, 370, 35);
        balancePanel.add(lblBalance);

        Account account;
        try {
            account = this.accountDao.getAccountByCustomerId(customer.getCustomerId());
            if (account != null) {
                balanceAmount = account.getBalance();
                lblBalance.setText("Account Balance: " + String.format("%.2f", balanceAmount) + " MMK");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // or show an error dialog
            lblBalance.setText("Account Balance: Error loading");
        }
        
        JButton eyeButton = new JButton();
        eyeButton.setBounds(410, 22, 35, 35);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setBorderPainted(false);
        eyeButton.setFocusPainted(false);
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
                lblBalance.setText(balanceVisible ? "Account Balance : " + String.format("%.2f", balanceAmount) + " MMK" :
                        "Account Balance : ****** MMK");
            });
        }
        balancePanel.add(eyeButton);
        panel.add(phonePanel);
        panel.add(balancePanel);
        
        // Labels and Inputs with larger fonts
        addLabel(panel, "To", 70, 265, 20); // Label font size increased to 20

        // +95 TextField 
        JTextField txtCountryCode = new JTextField("+95");
        txtCountryCode.setBounds(70, 305, 60, 55);
        txtCountryCode.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        txtCountryCode.setForeground(Color.BLACK);
        txtCountryCode.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        txtCountryCode.setEditable(false); 
        txtCountryCode.setFocusable(false); // focus 
        panel.add(txtCountryCode);

        // Phone number TextField
        txtTo = new JTextField();
        txtTo.setBounds(130, 305, 540, 55); // +95 field 
        txtTo.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        txtTo.setForeground(Color.GRAY);
        txtTo.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        txtTo.setText("Enter Phone Number");
        txtTo.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { 
                if (txtTo.getText().equals("Enter Phone Number")) { 
                    txtTo.setText(""); 
                    txtTo.setForeground(Color.BLACK); 
                } 
            }
            public void focusLost(FocusEvent e) { 
                if (txtTo.getText().isEmpty()) { 
                    txtTo.setText("Enter Phone Number"); 
                    txtTo.setForeground(Color.GRAY); 
                }
            }
        });

        // Phone number validation - 10 digits only
        txtTo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                
                if (!Character.isDigit(c) && 
                    c != KeyEvent.VK_BACK_SPACE && 
                    c != KeyEvent.VK_DELETE) {
                    e.consume();
                    return;
                }
                
                // 10 digits 
                String currentText = txtTo.getText();
                if (currentText.length() >= 10 && txtTo.getSelectedText() == null) {
                    e.consume();
                }
            }
        });

        panel.add(txtTo);


        addLabel(panel, "Amount", 70, 375, 20);
        txtAmount = new JTextField();
        txtAmount.setBounds(70, 410, 600, 55);
        txtAmount.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        txtAmount.setForeground(Color.BLACK);
        txtAmount.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 70));
        txtAmount.setText("0");
        txtAmount.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { if (txtAmount.getText().equals("0")) { txtAmount.setText(""); } }
            public void focusLost(FocusEvent e) { if (txtAmount.getText().isEmpty()) { txtAmount.setText("0"); } }
        });
        panel.add(txtAmount);
        
        JLabel mmk = new JLabel(" ");
        mmk.setBounds(590, 395, 60, 55);
        mmk.setForeground(Color.GRAY);
        mmk.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(mmk);

        addLabel(panel, "Description", 70, 480, 20);
        txtDescription = new JTextField();
        txtDescription.setBounds(70, 520, 600, 55);
        txtDescription.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        txtDescription.setForeground(Color.GRAY);
        txtDescription.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        txtDescription.setText("Optional note");
        txtDescription.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) { if (txtDescription.getText().equals("Optional note")) { txtDescription.setText(""); txtDescription.setForeground(Color.BLACK); } }
            public void focusLost(FocusEvent e) { if (txtDescription.getText().isEmpty()) { txtDescription.setText("Optional note"); txtDescription.setForeground(Color.GRAY); } }
        });
        panel.add(txtDescription);

        // Transfer Button
        JButton btnTransfer = new RoundedButton("Transfer");
        btnTransfer.setBounds(270, 605, 180, 60);
        btnTransfer.setBackground(new Color(0, 191, 255));
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Font size increased
        btnTransfer.setFocusPainted(false);
        btnTransfer.addActionListener(e -> handleTransfer());
        panel.add(btnTransfer);

        return panel;
    }

    // ================= Transfer Logic =================
    private void handleTransfer() {
        String phoneNumber = txtTo.getText().trim();
        String amountStr = txtAmount.getText().trim();

        // Validation
        if (phoneNumber.isEmpty() || phoneNumber.equals("Enter Phone Number")) {
            JOptionPane.showMessageDialog(this, "Please enter recipient Phone Number.");
            txtTo.requestFocus();
            return;
        }

        // Phone number validation - 10 digits only
        if (phoneNumber.length() != 10) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits.");
            txtTo.requestFocus();
            return;
        }


        if (!phoneNumber.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number.");
            txtTo.requestFocus();
            return;
        }

        if (amountStr.isEmpty() || amountStr.equals("0")) {
            JOptionPane.showMessageDialog(this, "Please enter amount.");
            txtAmount.requestFocus(); return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0.");
                return;
            }
            if (amount > balanceAmount) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!");
                return;
            }

            balanceAmount -= amount;
            if (balanceVisible) lblBalance.setText("Account Balance : " + String.format("%.2f", balanceAmount));

            String description = txtDescription.getText().trim();
            if (description.equals("Optional note")) description = "";

            Account account;
            try {
                account = accountDao.getAccountByNumber(phoneNumber);
                if (!account.getStatus().equals("CLOSED")) {
                    TransferDao transferDao = new TransferDao(DBConnection.getConnection());
                    Transaction tx = transferDao.createTransfer(customer, Long.parseLong(phoneNumber), amount, description);

                    if (tx != null) {
                        String message = String.format("Successfully transferred %,.2f MMK to %s", amount, phoneNumber);
                        if (!description.isEmpty()) message += "\nDescription: " + description;
                        JOptionPane.showMessageDialog(this, message);
                        txtTo.setText("Enter ID");
                        txtTo.setForeground(Color.GRAY);
                        txtAmount.setText("0");
                        txtDescription.setText("Optional note");
                        txtDescription.setForeground(Color.GRAY);
                    } else {
                        JOptionPane.showMessageDialog(this, "No account found with this account number!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "This account is currently closed! Try to connect in another way!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // ================= Helpers =================
    private void addLabel(JPanel p, String t, int x, int y, int size) {
        JLabel l = new JLabel(t);
        l.setBounds(x, y, 200, 30);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Tw Cen MT", Font.BOLD, size));
        p.add(l);
    }

    private JLabel createScaledImageLabel(String path, int w, int h) {
        URL url = getClass().getResource(path);
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(img));
        }
        return new JLabel();
    }

    private JPanel createRoundedPanel(Color c) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 80, 80);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private class RoundedButton extends JButton {
        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor;
        RoundedButton(String t) {
            super(t);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        public void setHoverBackgroundColor(Color color) { this.hoverBackgroundColor = color; }
        public void setPressedBackgroundColor(Color color) { this.pressedBackgroundColor = color; }
        
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
            new TransferPage(customer, customerDao).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> TransferPage.launch(customer, customerDao));
    }

}
