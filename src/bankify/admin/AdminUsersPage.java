package bankify.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersPage extends JFrame {
    private List<User> users = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable usersTable;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JButton backButton;

    public AdminUsersPage() {
        setTitle("Bankify - User Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());
        
        // Initialize sample data
        initializeSampleData();
        
        initComponents();
    }
    
    private void initComponents() {
        // Sidebar
        AdminSidebar sidebar = new AdminSidebar(this, "Admin");
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
        JLabel titleLabel = new JLabel("Users Management");
        titleLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage all user accounts and permissions");
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
        
        searchButton.addActionListener(e -> searchUsers());
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Filter ComboBox
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(new Color(255, 255, 255));
        filterPanel.setLayout(new BorderLayout());
        filterPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        filterPanel.setPreferredSize(new Dimension(200, 40));
        
        filterComboBox = new JComboBox<>(new String[]{"All Users", "Active", "Inactive", "Admin", "Customer", "Agent"});
        filterComboBox.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        filterComboBox.addActionListener(e -> filterUsers());
        
        filterPanel.add(filterComboBox, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton addButton = createButton("Add User", new Color(39, 174, 96));
        JButton editButton = createButton("Edit User", new Color(41, 128, 185));
        JButton deleteButton = createButton("Delete User", new Color(231, 76, 60));
        
        
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        
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
        String[] columns = {"ID", "Name", "Email", "Phone", "Role", "Status", "Join Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) {
                    return JButton.class;
                }
                return Object.class;
            }
        };
        
        // Create table
        usersTable = new JTable(tableModel);
        usersTable.setRowHeight(50);
        usersTable.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style table header
        JTableHeader header = usersTable.getTableHeader();
        header.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        header.setBackground(new Color(30, 127, 179));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        
        // Custom renderer for Status column
        usersTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        // Set column widths
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Role
        usersTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        usersTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Join Date
        
        
        // Populate table with data
        populateTable();
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    
       
   
    
    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing data
        
        for (User user : users) {
            Object[] rowData = {
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getJoinDate(),
                "Manage"  // This will be displayed as a button
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void searchUsers() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            populateTable();
            return;
        }
        
        tableModel.setRowCount(0);
        for (User user : users) {
            if (user.getName().toLowerCase().contains(searchText) ||
                user.getEmail().toLowerCase().contains(searchText) ||
                (user.getPhone() != null && user.getPhone().contains(searchText))) {
                
                Object[] rowData = {
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.getStatus(),
                    user.getJoinDate(),
                    "Manage"
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void filterUsers() {
        String filter = (String) filterComboBox.getSelectedItem();
        tableModel.setRowCount(0);
        
        for (User user : users) {
            boolean matches = false;
            
            switch (filter) {
                case "All Users":
                    matches = true;
                    break;
                case "Active":
                    matches = "Active".equals(user.getStatus());
                    break;
                case "Inactive":
                    matches = "Inactive".equals(user.getStatus());
                    break;
                case "Admin":
                    matches = "Admin".equals(user.getRole());
                    break;
                case "Customer":
                    matches = "Customer".equals(user.getRole());
                    break;
                case "Agent":
                    matches = "Agent".equals(user.getRole());
                    break;
            }
            
            if (matches) {
                Object[] rowData = {
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.getStatus(),
                    user.getJoinDate(),
                    "Manage"
                };
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void addUser() {
        UserDialog dialog = new UserDialog(this, "Add New User", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            User newUser = dialog.getUser();
            users.add(newUser);
            populateTable();
            JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User userToEdit = users.stream()
            .filter(u -> u.getId() == userId)
            .findFirst()
            .orElse(null);
        
        if (userToEdit != null) {
            UserDialog dialog = new UserDialog(this, "Edit User", userToEdit);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                User updatedUser = dialog.getUser();
                userToEdit.setName(updatedUser.getName());
                userToEdit.setEmail(updatedUser.getEmail());
                userToEdit.setPhone(updatedUser.getPhone());
                userToEdit.setRole(updatedUser.getRole());
                userToEdit.setStatus(updatedUser.getStatus());
                
                populateTable();
                JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + userName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            users.removeIf(u -> u.getId() == userId);
            populateTable();
            JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void exportUsers() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Users");
        fileChooser.setSelectedFile(new java.io.File("users_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Simple CSV export implementation
            try {
                java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile());
                writer.println("ID,Name,Email,Phone,Role,Status,Join Date");
                
                for (User user : users) {
                    writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),
                        user.getStatus(),
                        user.getJoinDate());
                }
                
                writer.close();
                JOptionPane.showMessageDialog(this, 
                    "Users exported successfully to: " + fileChooser.getSelectedFile().getPath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting users: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    
    private void initializeSampleData() {
        users.add(new User(1, "John Doe", "john.doe@email.com", "1234567890", "Admin", "Active", "2024-01-15"));
        users.add(new User(2, "Jane Smith", "jane.smith@email.com", "0987654321", "Customer", "Active", "2024-01-20"));
        users.add(new User(3, "Robert Johnson", "robert.j@email.com", "1122334455", "Agent", "Active", "2024-02-01"));
        users.add(new User(4, "Alice Brown", "alice.b@email.com", "5566778899", "Customer", "Inactive", "2024-02-10"));
        users.add(new User(5, "Charlie Wilson", "charlie.w@email.com", "6677889900", "Admin", "Active", "2024-02-15"));
        
    }
    
    // Custom cell renderer for Status column using DefaultTableCellRenderer
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            if ("Active".equals(value)) {
                label.setBackground(new Color(39, 174, 96));
                label.setForeground(Color.WHITE);
            } else if ("Inactive".equals(value)) {
                label.setBackground(new Color(231, 76, 60));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
            
            return label;
        }
    }
    
    // User Dialog for Add/Edit
    class UserDialog extends JDialog {
        private boolean confirmed = false;
        private User user;
        
        private JTextField nameField, emailField, phoneField;
        private JComboBox<String> roleCombo, statusCombo;
        
        public UserDialog(JFrame parent, String title, User existingUser) {
            super(parent, title, true);
            setSize(500, 400);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            formPanel.setBackground(Color.WHITE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Name
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Name:"), gbc);
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
            
            // Role
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Role:"), gbc);
            gbc.gridx = 1;
            roleCombo = new JComboBox<>(new String[]{"Admin", "Customer", "Agent"});
            roleCombo.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(roleCombo, gbc);
            
            // Status
            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
            statusCombo.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
            formPanel.add(statusCombo, gbc);
            
            // If editing existing user, populate fields
            if (existingUser != null) {
                nameField.setText(existingUser.getName());
                emailField.setText(existingUser.getEmail());
                phoneField.setText(existingUser.getPhone());
                roleCombo.setSelectedItem(existingUser.getRole());
                statusCombo.setSelectedItem(existingUser.getStatus());
                user = existingUser;
            } else {
                user = new User();
            }
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton saveButton = new RoundedButton2("Save", new Color(39, 174, 96));
            saveButton.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
            saveButton.setPreferredSize(new Dimension(100, 35));
            saveButton.addActionListener(e -> {
                confirmed = true;
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setPhone(phoneField.getText());
                user.setRole((String) roleCombo.getSelectedItem());
                user.setStatus((String) statusCombo.getSelectedItem());
                
                if (existingUser == null) {
                    user.setId(users.size() + 1);
                    user.setJoinDate("2024-03-10");
                }
                
                dispose();
            });
            
            JButton cancelButton = new RoundedButton2("Cancel", new Color(231, 76, 60));
            cancelButton.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
            cancelButton.setPreferredSize(new Dimension(100, 35));
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        public boolean isConfirmed() { return confirmed; }
        public User getUser() { return user; }
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
    
    // User class
    class User {
        private int id;
        private String name;
        private String email;
        private String phone;
        private String role;
        private String status;
        private String joinDate;
        
        // Default constructor
        public User() {}
        
        // Full constructor
        public User(int id, String name, String email, String phone, String role, String status, String joinDate) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.role = role;
            this.status = status;
            this.joinDate = joinDate;
        }
        
        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getJoinDate() { return joinDate; }
        public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminUsersPage frame = new AdminUsersPage();
            frame.setVisible(true);
        });
    }
}