package bankify.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AdminTransactionsPage extends JFrame {
    
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterType, filterStatus;
    private JTextField searchField;
    
    // Sample data for demo
    private List<Transaction> transactionList;
    
    public AdminTransactionsPage() {
        setTitle("Bankify - Transaction Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        
        initData(); // Initialize sample data
        initComponents();
        loadTransactions();
    }
    
    private void initData() {
        transactionList = new ArrayList<>();
        
        // Add sample transactions
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        transactionList.add(new Transaction("TXN001", "U001", "John Doe", "U002", "Jane Smith", 
                "Transfer", 50000.0, "Successful", new Date()));
        
        transactionList.add(new Transaction("TXN003", "U005", "Charlie Lee", "U001", "John Doe", 
                "Deposit", 100000.0, "Pending", new Date()));
        transactionList.add(new Transaction("TXN004", "U002", "Jane Smith", "U006", "David Kim", 
                "Withdrawal", 30000.0, "Successful", new Date()));
        transactionList.add(new Transaction("TXN005", "U004", "Bob Wilson", "U003", "Alice Brown", 
                "Transfer", 15000.0, "Failed", new Date()));
        
        transactionList.add(new Transaction("TXN007", "U001", "John Doe", "U003", "Alice Brown", 
                "Transfer", 45000.0, "Successful", new Date()));
        
    }
    
    private void initComponents() {
        // Sidebar
        AdminSidebar sidebar = new AdminSidebar(this, "Transactions");
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
        JLabel titleLabel = new JLabel("Transaction Management");
        titleLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("View and manage all financial transactions");
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
        
        searchButton.addActionListener(e -> searchTransactions());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Filter Panels
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(240, 242, 245));
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        // Type Filter
        JPanel typeFilterPanel = new JPanel();
        typeFilterPanel.setBackground(new Color(255, 255, 255));
        typeFilterPanel.setLayout(new BorderLayout());
        typeFilterPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        typeFilterPanel.setPreferredSize(new Dimension(200, 40));
        
        filterType = new JComboBox<>(new String[]{"All Types", "Transfer", "Deposit", "Withdrawal"});
        filterType.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        filterType.setBackground(Color.WHITE);
        filterType.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        filterType.addActionListener(e -> filterTransactions()); // Auto-filter when changed
        
        typeFilterPanel.add(filterType, BorderLayout.CENTER);
        
        // Status Filter
        JPanel statusFilterPanel = new JPanel();
        statusFilterPanel.setBackground(new Color(255, 255, 255));
        statusFilterPanel.setLayout(new BorderLayout());
        statusFilterPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        statusFilterPanel.setPreferredSize(new Dimension(200, 40));
        
        filterStatus = new JComboBox<>(new String[]{"All Status", "Successful", "Pending", "Failed"});
        filterStatus.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        filterStatus.setBackground(Color.WHITE);
        filterStatus.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        filterStatus.addActionListener(e -> filterTransactions()); // Auto-filter when changed
        
        statusFilterPanel.add(filterStatus, BorderLayout.CENTER);
        
        // View Details Button
        JButton viewDetailsButton = createButton("View Details", new Color(39, 174, 96));
        viewDetailsButton.addActionListener(e -> viewSelectedTransaction());
        
        // Reset Button
        JButton resetButton = createButton("Reset", new Color(231, 76, 60));
        resetButton.addActionListener(e -> resetFilter());
        
        filterPanel.add(typeFilterPanel);
        filterPanel.add(statusFilterPanel);
        filterPanel.add(viewDetailsButton);
        filterPanel.add(resetButton);
        
        
        
        toolbarPanel.add(searchPanel);
        toolbarPanel.add(filterPanel);
        toolbarPanel.add(buttonPanel);
        
        return toolbarPanel;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new RoundedButton2(text, color);
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
        String[] columns = {"Transaction ID", "From User", "To User", "Type", "Amount", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table
        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(50);
        transactionTable.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style table header
        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        header.setBackground(new Color(30, 127, 179));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        
        // Custom renderer for Status column
        StatusRenderer statusRenderer = new StatusRenderer();
        transactionTable.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);
        
        // Center align other columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            if (i != 4 && i != 5) { // Skip Amount and Status columns
                transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Custom renderer for Amount column (right align)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        transactionTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Transaction ID
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(180); // From User
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(180); // To User
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Type
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Amount
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Date
        
        // Populate table with data
        loadTransactions();
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void loadTransactions() {
        tableModel.setRowCount(0); // Clear existing data
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Transaction txn : transactionList) {
            Object[] row = {
                txn.getTransactionId(),
                txn.getFromUserName() + " (" + txn.getFromUserId() + ")",
                txn.getToUserName() + " (" + txn.getToUserId() + ")",
                txn.getType(),
                String.format("MMK %,.0f", txn.getAmount()),
                txn.getStatus(),
                sdf.format(txn.getDate())
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchTransactions() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadTransactions();
            return;
        }
        
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Transaction txn : transactionList) {
            if (txn.getTransactionId().toLowerCase().contains(searchText) ||
                txn.getFromUserId().toLowerCase().contains(searchText) ||
                txn.getFromUserName().toLowerCase().contains(searchText) ||
                txn.getToUserId().toLowerCase().contains(searchText) ||
                txn.getToUserName().toLowerCase().contains(searchText) ||
                txn.getType().toLowerCase().contains(searchText)) {
                
                Object[] row = {
                    txn.getTransactionId(),
                    txn.getFromUserName() + " (" + txn.getFromUserId() + ")",
                    txn.getToUserName() + " (" + txn.getToUserId() + ")",
                    txn.getType(),
                    String.format("MMK %,.0f", txn.getAmount()),
                    txn.getStatus(),
                    sdf.format(txn.getDate())
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void filterTransactions() {
        String typeFilter = (String) filterType.getSelectedItem();
        String statusFilter = (String) filterStatus.getSelectedItem();
        
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Transaction txn : transactionList) {
            boolean typeMatch = typeFilter.equals("All Types") || txn.getType().equals(typeFilter);
            boolean statusMatch = statusFilter.equals("All Status") || txn.getStatus().equals(statusFilter);
            
            if (typeMatch && statusMatch) {
                Object[] row = {
                    txn.getTransactionId(),
                    txn.getFromUserName() + " (" + txn.getFromUserId() + ")",
                    txn.getToUserName() + " (" + txn.getToUserId() + ")",
                    txn.getType(),
                    String.format("MMK %,.0f", txn.getAmount()),
                    txn.getStatus(),
                    sdf.format(txn.getDate())
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void resetFilter() {
        filterType.setSelectedIndex(0);
        filterStatus.setSelectedIndex(0);
        searchField.setText("");
        loadTransactions();
    }
    
    private void viewSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction first!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        selectedRow = transactionTable.convertRowIndexToModel(selectedRow);
        showTransactionDetails(selectedRow);
    }
    
    private void showTransactionDetails(int rowIndex) {
        String txnId = (String) tableModel.getValueAt(rowIndex, 0);
        String fromUser = (String) tableModel.getValueAt(rowIndex, 1);
        String toUser = (String) tableModel.getValueAt(rowIndex, 2);
        String type = (String) tableModel.getValueAt(rowIndex, 3);
        String amount = (String) tableModel.getValueAt(rowIndex, 4);
        String status = (String) tableModel.getValueAt(rowIndex, 5);
        String date = (String) tableModel.getValueAt(rowIndex, 6);
        
        String details = String.format(
            "<html><body style='width: 450px'>" +
            "<h2 style='color: #1E7FB3; margin-bottom: 20px;'>Transaction Details</h2>" +
            "<table border='0' cellpadding='8' style='font-size: 14px;'>" +
            "<tr><td style='font-weight: bold; width: 120px;'>Transaction ID:</td><td>%s</td></tr>" +
            "<tr><td style='font-weight: bold;'>From User:</td><td>%s</td></tr>" +
            "<tr><td style='font-weight: bold;'>To User:</td><td>%s</td></tr>" +
            "<tr><td style='font-weight: bold;'>Type:</td><td>%s</td></tr>" +
            "<tr><td style='font-weight: bold;'>Amount:</td><td><b style='font-size: 16px;'>%s</b></td></tr>" +
            "<tr><td style='font-weight: bold;'>Status:</td><td><span style='color: %s; font-weight: bold;'>%s</span></td></tr>" +
            "<tr><td style='font-weight: bold;'>Date:</td><td>%s</td></tr>" +
            "</table></body></html>",
            txnId, fromUser, toUser, type, amount, 
            getStatusColor(status), status, date
        );
        
        JOptionPane.showMessageDialog(this, details, "Transaction Details", 
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getStatusColor(String status) {
        switch (status) {
            case "Successful": return "#27ae60"; // Green
            case "Pending": return "#f39c12"; // Orange
            case "Failed": return "#dc3545"; // Red
            default: return "#000000";
        }
    }
    
    
    
    private void processPendingTransactions() {
        int pendingCount = 0;
        for (Transaction txn : transactionList) {
            if (txn.getStatus().equals("Pending")) {
                txn.setStatus("Successful");
                pendingCount++;
            }
        }
        
        if (pendingCount > 0) {
            JOptionPane.showMessageDialog(this, 
                    pendingCount + " pending transactions have been processed.",
                    "Transactions Processed", 
                    JOptionPane.INFORMATION_MESSAGE);
            loadTransactions();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "No pending transactions found.",
                    "No Pending Transactions", 
                    JOptionPane.INFORMATION_MESSAGE);
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
            setFont(new Font("Tw Cen MT", Font.BOLD, 14));
            
            String status = value.toString();
            Color bgColor = Color.WHITE;
            Color fgColor = Color.BLACK;
            
            switch (status) {
                case "Successful":
                    bgColor = new Color(39, 174, 96); // Green
                    fgColor = Color.WHITE;
                    break;
                case "Pending":
                    bgColor = new Color(243, 156, 18); // Orange
                    fgColor = Color.WHITE;
                    break;
                case "Failed":
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // Transaction Model Class
    private class Transaction {
        private String transactionId;
        private String fromUserId;
        private String fromUserName;
        private String toUserId;
        private String toUserName;
        private String type;
        private double amount;
        private String status;
        private Date date;
        
        public Transaction(String transactionId, String fromUserId, String fromUserName,
                          String toUserId, String toUserName, String type,
                          double amount, String status, Date date) {
            this.transactionId = transactionId;
            this.fromUserId = fromUserId;
            this.fromUserName = fromUserName;
            this.toUserId = toUserId;
            this.toUserName = toUserName;
            this.type = type;
            this.amount = amount;
            this.status = status;
            this.date = date;
        }
        
        // Getters and Setters
        public String getTransactionId() { return transactionId; }
        public String getFromUserId() { return fromUserId; }
        public String getFromUserName() { return fromUserName; }
        public String getToUserId() { return toUserId; }
        public String getToUserName() { return toUserName; }
        public String getType() { return type; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
        public Date getDate() { return date; }
        
        public void setStatus(String status) { this.status = status; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminTransactionsPage frame = new AdminTransactionsPage();
            frame.setVisible(true);
        });
    }

}
