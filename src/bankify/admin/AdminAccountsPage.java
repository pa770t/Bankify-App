package bankify.admin;

import bankify.Agent;
import bankify.dao.AdminDao;
import bankify.dao.AgentDao;

import javax.swing.*;
import javax.swing.table.*;



import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AdminAccountsPage extends JFrame {
    
    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    private static Connection conn;
    private static Agent agent;
    private static AgentDao agentDao;
    
    // Sample data for demo
    private List<Account> accountList;
    private static AdminDao adminDao;
    
    public AdminAccountsPage(Agent ag, AgentDao agd, Connection connection) {
        agent = ag;
        agentDao = agd;
        conn = connection;
        adminDao = new AdminDao(conn);

        setTitle("Bankify - Accounts Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        
        initData(); // Initialize sample data
        initComponents();
        loadAccounts();
    }
    
    private void initData() {
        accountList = new ArrayList<>();
        
        // Add sample accounts
        accountList.add(new Account("ACC001", "John Doe", "johndoe@email.com", 
                "09-123456789", "Normal User", 1500000.0, "Active", "2023-01-15"));
        
        accountList.add(new Account("ACC002", "Jane Smith", "janesmith@email.com", 
                "09-987654321", "Normal User", 2500000.0, "Active", "2023-02-20"));
        
        accountList.add(new Account("ACC003", "Alice Brown", "aliceb@email.com", 
                "09-555666777", "Agent", 750000.0, "Inactive", "2023-03-10"));
        
        accountList.add(new Account("ACC004", "Bob Wilson", "bobw@email.com", 
                "09-888999000", "Agent", 5000000.0, "Active", "2023-04-05"));
        
        accountList.add(new Account("ACC005", "Charlie Lee", "charlie@email.com", 
                "09-111222333", "Normal User", 1200000.0, "Active", "2023-05-12"));
        
        accountList.add(new Account("ACC006", "David Kim", "davidk@email.com", 
                "09-444555666", "Agent", 1800000.0, "Inactive", "2023-06-18"));
    }
    
    private void initComponents() {
        // Sidebar
        AdminSidebar sidebar = new AdminSidebar(this, "Accounts", agent, agentDao, conn);
        sidebar.setBackground(new Color(255, 255, 255));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 242, 245));
        contentPanel.setLayout(new BorderLayout(0, 20));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Toolbar Panel (Search and Filters)
        JPanel toolbarPanel = createToolbarPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Add all panels to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(toolbarPanel, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.SOUTH);
        
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 127, 179));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
        headerPanel.setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Accounts Management");
        titleLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage all customer accounts");
        subtitleLabel.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    private JPanel createToolbarPanel() {
        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setBackground(new Color(240, 242, 245));
        toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 13));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(255, 255, 255));
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        searchPanel.setPreferredSize(new Dimension(300, 40));
        
        searchField = new JTextField();
        searchField.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.setPreferredSize(new Dimension(250, 40));
        
        JButton searchButton = new JButton();
        searchButton.setBackground(new Color(30, 127, 179));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setPreferredSize(new Dimension(40, 40));
        
        try {
            ImageIcon searchIcon = new ImageIcon(getClass().getResource("/Resources/search_icon.png"));
            if (searchIcon.getImage() != null) {
                Image img = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                searchButton.setIcon(new ImageIcon(img));
            } else {
                searchButton.setText("🔍");
            }
        } catch (Exception e) {
            searchButton.setText("🔍");
        }
        
        searchButton.addActionListener(e -> searchAccounts());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Filter ComboBox
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(255, 255, 255));
        filterPanel.setLayout(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        filterPanel.setPreferredSize(new Dimension(200, 40));
        
        filterComboBox = new JComboBox<>(new String[]{"All Accounts", "Active", "Inactive", "Normal User", "Agent"});
        filterComboBox.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        filterComboBox.addActionListener(e -> filterAccounts());
        
        filterPanel.add(filterComboBox, BorderLayout.CENTER);
        
        // Action Buttons 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
//        JButton addButton = createButton("Add Account", new Color(39, 174, 96));
//        JButton editButton = createButton("Edit Account", new Color(41, 128, 185));
        JButton deleteButton = createButton("Delete Account", new Color(231, 76, 60));
        
//        addButton.addActionListener(e -> addNewAccount());
//        editButton.addActionListener(e -> editAccount());
        deleteButton.addActionListener(e -> deleteAccount());
        
//        buttonPanel.add(addButton);
//        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        toolbarPanel.add(searchPanel);
        toolbarPanel.add(filterPanel);
        toolbarPanel.add(buttonPanel);
        
        return toolbarPanel;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new RoundedButton(text, color);
        button.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        return button;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(new Color(240, 242, 245));
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        
        // Create table model
        String[] columns = {"Account ID", "Account Holder", "Email", "Phone", "Account Type", "Balance", "Status", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table
        accountsTable = new JTable(tableModel);
        accountsTable.setRowHeight(50);
        accountsTable.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style table header
        JTableHeader header = accountsTable.getTableHeader();
        header.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        header.setBackground(new Color(30, 127, 179));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        
        // Custom renderer for Status column (column 6)
        StatusRenderer statusRenderer = new StatusRenderer();
        accountsTable.getColumnModel().getColumn(6).setCellRenderer(statusRenderer); // Status column index 6
        
        // Center align for specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < accountsTable.getColumnCount(); i++) {
            if (i != 1 && i != 2 && i != 3 && i != 5 && i != 6) { 
                // Skip Name, Email, Phone, Balance, Status columns from center alignment
                accountsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Right align for Balance column (column 5)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        accountsTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        
        // Set column widths
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Account No
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Account Holder
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(160); // Email
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Phone
        accountsTable.getColumnModel().getColumn(4).setPreferredWidth(130); // Account Type
        accountsTable.getColumnModel().getColumn(5).setPreferredWidth(140); // Balance
        accountsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        accountsTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Created Date
        
        // Populate table with data
        loadAccounts();
        
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void loadAccounts() {
        tableModel.setRowCount(0); // Clear existing data
        accountList = adminDao.getAllAccounts();
        
        for (Account acc : accountList) {
            Object[] row = {
                acc.getAccountNumber(),
                acc.getAccountHolder(),
                acc.getEmail(),
                acc.getPhone(),
                acc.getAccountType(),
                String.format("MMK %,.0f", acc.getBalance()), // Format balance
                acc.getStatus(),
                acc.getCreatedDate()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchAccounts() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadAccounts();
            return;
        }
        
        tableModel.setRowCount(0);
        
        for (Account acc : accountList) {
            if (acc.getAccountNumber().toLowerCase().contains(searchText) ||
                acc.getAccountHolder().toLowerCase().contains(searchText) ||
                acc.getEmail().toLowerCase().contains(searchText) ||
                acc.getPhone().toLowerCase().contains(searchText) ||
                acc.getAccountType().toLowerCase().contains(searchText)) {
                
                Object[] row = {
                    acc.getAccountNumber(),
                    acc.getAccountHolder(),
                    acc.getEmail(),
                    acc.getPhone(),
                    acc.getAccountType(),
                    String.format("MMK %,.0f", acc.getBalance()),
                    acc.getStatus(),
                    acc.getCreatedDate()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void filterAccounts() {
        String filter = (String) filterComboBox.getSelectedItem();
        tableModel.setRowCount(0);
        
        for (Account acc : accountList) {
            boolean matches = false;
            
            switch (filter) {
                case "All Accounts":
                    matches = true;
                    break;
                case "Active":
                    matches = "Active".equals(acc.getStatus());
                    break;
                case "Inactive":
                    matches = "Inactive".equals(acc.getStatus());
                    break;
                case "Normal User":
                    matches = "Normal User".equals(acc.getAccountType());
                    break;
                case "Agent":
                    matches = "Agent".equals(acc.getAccountType());
                    break;
                
            }
            
            if (matches) {
                Object[] row = {
                    acc.getAccountNumber(),
                    acc.getAccountHolder(),
                    acc.getEmail(),
                    acc.getPhone(),
                    acc.getAccountType(),
                    String.format("MMK %,.0f", acc.getBalance()),
                    acc.getStatus(),
                    acc.getCreatedDate()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void addNewAccount() {
        AccountDialog dialog = new AccountDialog(this, "Add New Account", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Account newAccount = dialog.getAccount();
            accountList.add(newAccount);
            loadAccounts();
            JOptionPane.showMessageDialog(this, "Account added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String accountNo = (String) tableModel.getValueAt(selectedRow, 0);
        Account accountToEdit = accountList.stream()
            .filter(acc -> acc.getAccountNumber().equals(accountNo))
            .findFirst()
            .orElse(null);
        
        if (accountToEdit != null) {
            AccountDialog dialog = new AccountDialog(this, "Edit Account", accountToEdit);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Account updatedAccount = dialog.getAccount();
                accountToEdit.setAccountHolder(updatedAccount.getAccountHolder());
                accountToEdit.setEmail(updatedAccount.getEmail());
                accountToEdit.setPhone(updatedAccount.getPhone());
                accountToEdit.setAccountType(updatedAccount.getAccountType());
                accountToEdit.setBalance(updatedAccount.getBalance());
                accountToEdit.setStatus(updatedAccount.getStatus());
                
                loadAccounts();
                JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void deleteAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String accountNo = (String) tableModel.getValueAt(selectedRow, 0);
        String accountHolder = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete account: " + accountNo + " (" + accountHolder + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = adminDao.deleteAccount(accountNo);
            if (success) {
                accountList.removeIf(acc -> acc.getAccountNumber().equals(accountNo));
            }
            loadAccounts();
            JOptionPane.showMessageDialog(this, "Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
 // StatusRenderer class for Status column with background colors
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component cell = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            // Center align text
            setHorizontalAlignment(SwingConstants.CENTER);
            
            // Set font and bold
            setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            
            String status = value.toString();
            Color bgColor = Color.WHITE;
            Color fgColor = Color.BLACK;
            
            switch (status) {
                case "Active":
                    bgColor = new Color(39, 174, 96); // Green
                    fgColor = Color.WHITE;
                    break;
               
                case "Inactive":
                    bgColor = new Color(231, 76, 60); // Red
                    fgColor = Color.WHITE;
                    break;
                default:
                    bgColor = Color.WHITE;
                    fgColor = Color.BLACK;
            }
            
            // Status column ကို အမြဲတမ်း အရောင်အသေထား
            cell.setBackground(bgColor);
            cell.setForeground(fgColor);
            
            // Rounded border effect
            if (cell instanceof JLabel) {
                JLabel label = (JLabel) cell;
                label.setOpaque(true);
                
                // Add rounded border
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.darker(), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            
            return cell;
        }
    }
    
    // Account Dialog for Add/Edit
    class AccountDialog extends JDialog {
        private boolean confirmed = false;
        private Account account;
        
        private JTextField nameField, emailField, phoneField, balanceField;
        private JComboBox<String> typeCombo, statusCombo;
        
        public AccountDialog(JFrame parent, String title, Account existingAccount) {
            super(parent, title, true);
            setSize(500, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            formPanel.setBackground(Color.WHITE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Account Holder Name
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Account Holder:"), gbc);
            gbc.gridx = 1;
            nameField = new JTextField(20);
            nameField.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(nameField, gbc);
            
            // Email
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            emailField = new JTextField(20);
            emailField.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(emailField, gbc);
            
            // Phone
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Phone:"), gbc);
            gbc.gridx = 1;
            phoneField = new JTextField(20);
            phoneField.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(phoneField, gbc);
            
            // Account Type
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Account Type:"), gbc);
            gbc.gridx = 1;
            typeCombo = new JComboBox<>(new String[]{"Savings", "Current", "Fixed Deposit"});
            typeCombo.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(typeCombo, gbc);
            
            // Balance
            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("Balance:"), gbc);
            gbc.gridx = 1;
            balanceField = new JTextField(20);
            balanceField.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(balanceField, gbc);
            
            // Status
            gbc.gridx = 0; gbc.gridy = 5;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
            statusCombo.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(statusCombo, gbc);
            
            // If editing existing account, populate fields
            if (existingAccount != null) {
                nameField.setText(existingAccount.getAccountHolder());
                emailField.setText(existingAccount.getEmail());
                phoneField.setText(existingAccount.getPhone());
                typeCombo.setSelectedItem(existingAccount.getAccountType());
                balanceField.setText(String.valueOf(existingAccount.getBalance()));
                statusCombo.setSelectedItem(existingAccount.getStatus());
                account = existingAccount;
            } else {
                account = new Account();
            }
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton saveButton = new RoundedButton("Save", new Color(39, 174, 96));
            saveButton.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
            saveButton.setPreferredSize(new Dimension(100, 35));
            saveButton.addActionListener(e -> {
                if (validateForm()) {
                    confirmed = true;
                    account.setAccountHolder(nameField.getText());
                    account.setEmail(emailField.getText());
                    account.setPhone(phoneField.getText());
                    account.setAccountType((String) typeCombo.getSelectedItem());
                    account.setBalance(Double.parseDouble(balanceField.getText()));
                    account.setStatus((String) statusCombo.getSelectedItem());
                    
                    if (existingAccount == null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = sdf.format(new Date());
                        account.setAccountNumber("ACC" + String.format("%03d", accountList.size() + 1));
                        account.setCreatedDate(currentDate);
                    }
                    
                    dispose();
                }
            });
            
            JButton cancelButton = new RoundedButton("Cancel", new Color(231, 76, 60));
            cancelButton.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
            cancelButton.setPreferredSize(new Dimension(100, 35));
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private boolean validateForm() {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter account holder name", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email address", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter phone number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            try {
                double balance = Double.parseDouble(balanceField.getText());
                if (balance < 0) {
                    JOptionPane.showMessageDialog(this, "Balance cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid balance amount", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
        }
        
        public boolean isConfirmed() { return confirmed; }
        public Account getAccount() { return account; }
    }
    
    // RoundedButton class
    private class RoundedButton extends JButton {
        private Color currentColor;
        public RoundedButton(String text, Color baseColor) {
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Account Model Class
    public static class Account {
        private String accountNumber;
        private String accountHolder;
        private String email;
        private String phone;
        private String accountType;
        private double balance;
        private String status;
        private String createdDate;
        
        // Default constructor
        public Account() {}
        
        // Full constructor
        public Account(String accountNumber, String accountHolder, String email,
                      String phone, String accountType, double balance,
                      String status, String createdDate) {
            this.accountNumber = accountNumber;
            this.accountHolder = accountHolder;
            this.email = email;
            this.phone = phone;
            this.accountType = accountType;
            this.balance = balance;
            this.status = status;
            this.createdDate = createdDate;
        }
        
        // Getters
        public String getAccountNumber() { return accountNumber; }
        public String getAccountHolder() { return accountHolder; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAccountType() { return accountType; }
        public double getBalance() { return balance; }
        public String getStatus() { return status; }
        public String getCreatedDate() { return createdDate; }
        
        // Setters
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
        public void setBalance(double balance) { this.balance = balance; }
        public void setStatus(String status) { this.status = status; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminAccountsPage frame = new AdminAccountsPage(agent, agentDao, conn);
            frame.setVisible(true);
        });
    }

}
