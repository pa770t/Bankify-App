package bankify.admin;

import bankify.Agent;
import bankify.Customer;
import bankify.DBConnection;
import bankify.dao.AdminDao;
import bankify.dao.AgentDao;
import bankify.dao.CustomerDao;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersPage extends JFrame {

    private List<User> users = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable usersTable;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private static CustomerDao customerDao;
    private static AgentDao agentDao;
    private static AdminDao adminDao;
    private static Connection conn;

    public AdminUsersPage() {
        // In a real app, you would pass the connection, but this works for your setup
        conn = DBConnection.getConnection();
        adminDao = new AdminDao(conn);

        setTitle("Bankify - User Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        initializeSampleData();
        initComponents();
    }

    private void initComponents() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 800));
        sidebar.add(new JLabel("Sidebar Placeholder"));

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 242, 245));
        contentPanel.setLayout(new BorderLayout(0, 20));

        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        contentPanel.add(createToolbarPanel(), BorderLayout.CENTER);
        contentPanel.add(createTablePanel(), BorderLayout.SOUTH);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 127, 179));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Users Management");
        titleLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

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
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(255, 255, 255));
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        searchPanel.setPreferredSize(new Dimension(300, 40));
        searchField = new JTextField();
        searchField.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        searchField.setPreferredSize(new Dimension(250, 40));
        JButton searchButton = new JButton("🔍");
        searchButton.setBackground(new Color(30, 127, 179));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.setPreferredSize(new Dimension(40, 40));
        searchButton.addActionListener(e -> searchUsers());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Filter ComboBox
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(new Color(255, 255, 255));
        filterPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        filterPanel.setPreferredSize(new Dimension(200, 40));
        filterComboBox = new JComboBox<>(new String[]{"All Users", "Active", "Inactive", "Admin", "Customer", "Agent"});
        filterComboBox.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        filterComboBox.addActionListener(e -> filterUsers());
        filterPanel.add(filterComboBox, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(240, 242, 245));
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
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 242, 245));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        String[] columns = {"ID", "Name", "Email", "Phone", "Role", "Status", "Join Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setRowHeight(50);
        usersTable.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = usersTable.getTableHeader();
        header.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        header.setBackground(new Color(30, 127, 179));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));

        usersTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        populateTable();
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        for (User user : users) {
            Object[] rowData = {
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.getStatus(),
                    user.getJoinDate()
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
                        user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                        user.getRole(), user.getStatus(), user.getJoinDate()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void filterUsers() {
        String filter = (String) filterComboBox.getSelectedItem();
        tableModel.setRowCount(0);
        for (User user : users) {
            boolean matches = "All Users".equals(filter) ||
                    ("Active".equals(filter) && "Active".equals(user.getStatus())) ||
                    ("Inactive".equals(filter) && "Inactive".equals(user.getStatus())) ||
                    (user.getRole() != null && user.getRole().equals(filter));
            if (matches) {
                Object[] rowData = {
                        user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                        user.getRole(), user.getStatus(), user.getJoinDate()
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

            if ("Customer".equalsIgnoreCase(newUser.getRole())) {
                Customer dbCustomer = new Customer();
                dbCustomer.setFirstName(dialog.getFirstName());
                dbCustomer.setLastName(dialog.getLastName());
                dbCustomer.setEmail(dialog.getEmail());
                dbCustomer.setPhoneNumber(dialog.getPhone());
                dbCustomer.setAddress(dialog.getAddress()); // Set Address for DB


                // Hashing password
                String plainPassword = "TempPass@123";
                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
                dbCustomer.setPassword(hashedPassword);
                dbCustomer.setFirstTimeLogin(true);

                Customer createdCustomer = adminDao.createCustomerByAdmin(dbCustomer);
                if (createdCustomer != null) {
                    newUser.setId(createdCustomer.getCustomerId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save Customer to database!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            else if ("Agent".equalsIgnoreCase(newUser.getRole())) {
                Agent agent = new Agent();
                agent.setFullName(dialog.getFirstName() + " " + dialog.getLastName());
                agent.setEmail(dialog.getEmail());
                agent.setGender(newUser.getGender());
                agent.setRole("STAFF");
                agent.setPhoneNumber(dialog.getPhone());
                agent.setAddress(dialog.getAddress());

                // Hashing password
                String plainPassword = "TempPass@123";
                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
                agent.setPassword(hashedPassword);

                Agent createdAgent = adminDao.createAgent(agent.getFullName(), agent.getRole(), agent.getGender(),
                        agent.getEmail(), agent.getPhoneNumber(), agent.getAddress(), agent.getPassword());
                if (createdAgent != null) {
                    newUser.setId(createdAgent.getAgentId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save Customer to database!", "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Logic for other roles can be added here
            // else if ("Agent".equalsIgnoreCase(newUser.getRole())) { ... }

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
        User userToEdit = users.stream().filter(u -> u.getId() == userId).findFirst().orElse(null);

        if (userToEdit != null) {
            UserDialog dialog = new UserDialog(this, "Edit User", userToEdit);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                User updatedUser = dialog.getUser();
                userToEdit.setName(updatedUser.getName());
                userToEdit.setEmail(updatedUser.getEmail());
                userToEdit.setPhone(updatedUser.getPhone());
                userToEdit.setAddress(updatedUser.getAddress()); // Update address
                userToEdit.setGender(updatedUser.getGender());
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
                "Are you sure you want to delete user: " + userName + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            users.removeIf(u -> u.getId() == userId);
            populateTable();
            JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initializeSampleData() {
        this.users = adminDao.getAllUsers();
    }

    // StatusRenderer class
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Tw Cen MT", Font.PLAIN, 14));

            String status = String.valueOf(value);
            Color bgColor = "Active".equals(status) ? new Color(39, 174, 96) : new Color(231, 76, 60);
            cell.setBackground(bgColor);
            cell.setForeground(Color.WHITE);

            if (cell instanceof JLabel) {
                ((JLabel) cell).setOpaque(true);
            }
            return cell;
        }
    }

    // User Dialog for Add/Edit
    class UserDialog extends JDialog {
        private boolean confirmed = false;
        private User user;
        private JTextField firstNameField, lastNameField, emailField, phoneField, addressField; // Added addressField
        private JComboBox<String> genderCombo, roleCombo, statusCombo;

        public UserDialog(JFrame parent, String title, User existingUser) {
            super(parent, title, true);
            setSize(520, 550); // Increased Height for address
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            formPanel.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.gridx = 0;
            gbc.ipady = 10;

            // First Name
            gbc.gridy = 0; formPanel.add(new JLabel("First Name:"), gbc);
            gbc.gridx = 1; firstNameField = new JTextField(20); formPanel.add(firstNameField, gbc);
            // Last Name
            gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Last Name:"), gbc);
            gbc.gridx = 1; lastNameField = new JTextField(20); formPanel.add(lastNameField, gbc);
            // Email
            gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; emailField = new JTextField(20); formPanel.add(emailField, gbc);
            // Phone
            gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Phone:"), gbc);
            gbc.gridx = 1; phoneField = new JTextField(20); formPanel.add(phoneField, gbc);
            // Address (NEW FIELD)
            gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1; addressField = new JTextField(20); formPanel.add(addressField, gbc);
            // Gender
            gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Gender:"), gbc);
            gbc.gridx = 1; genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "LGBTQ+"}); formPanel.add(genderCombo, gbc);
            // Role
            gbc.gridx = 0; gbc.gridy = 6; formPanel.add(new JLabel("Role:"), gbc);
            gbc.gridx = 1; roleCombo = new JComboBox<>(new String[]{"Admin", "Customer", "Agent"}); formPanel.add(roleCombo, gbc);
            // Status
            gbc.gridx = 0; gbc.gridy = 7; formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1; statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"}); formPanel.add(statusCombo, gbc);

            // Populate data if editing
            if (existingUser != null) {
                String[] nameParts = existingUser.getName().split(" ", 2);
                firstNameField.setText(nameParts[0]);
                lastNameField.setText(nameParts.length > 1 ? nameParts[1] : "");
                emailField.setText(existingUser.getEmail());
                phoneField.setText(existingUser.getPhone());
                addressField.setText(existingUser.getAddress()); // Set Address
                genderCombo.setSelectedItem(existingUser.getGender());
                roleCombo.setSelectedItem(existingUser.getRole());
                statusCombo.setSelectedItem(existingUser.getStatus());
                user = existingUser;
            } else {
                user = new User();
            }

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
            buttonPanel.setBackground(Color.WHITE);
            JButton saveButton = new RoundedButton2("Save", new Color(39, 174, 96));
            saveButton.addActionListener(e -> {
                if (!validateForm()) return;
                confirmed = true;
                user.setName(firstNameField.getText().trim() + " " + lastNameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim());
                user.setAddress(addressField.getText().trim()); // Get Address
                user.setGender((String) genderCombo.getSelectedItem());
                user.setRole((String) roleCombo.getSelectedItem());
                user.setStatus((String) statusCombo.getSelectedItem());

                if (existingUser == null) {
                    user.setId(users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1);
                    user.setJoinDate(java.time.LocalDate.now().toString());
                }
                dispose();
            });
            JButton cancelButton = new RoundedButton2("Cancel", new Color(231, 76, 60));
            cancelButton.addActionListener(e -> dispose());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private boolean validateForm() {
            if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                showError("First Name and Last Name are required."); return false;
            }
            if (emailField.getText().trim().isEmpty() || !emailField.getText().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showError("Please enter a valid email address."); return false;
            }
            if (phoneField.getText().trim().isEmpty() || !phoneField.getText().trim().matches("\\d{6,15}")) {
                showError("Phone number must contain 6 to 15 digits."); return false;
            }
            if (addressField.getText().trim().isEmpty()) { // Validate Address
                showError("Address is required."); return false;
            }
            return true;
        }

        private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE); }

        public String getFirstName() { return firstNameField.getText().trim(); }
        public String getLastName() { return lastNameField.getText().trim(); }
        public String getEmail() { return emailField.getText().trim(); }
        public String getPhone() { return phoneField.getText().trim(); }
        public String getAddress() { return addressField.getText().trim(); } // Getter for Address
        public boolean isConfirmed() { return confirmed; }
        public User getUser() { return user; }
    }

    // RoundedButton2 class
    private class RoundedButton2 extends JButton {
        private Color currentColor;
        public RoundedButton2(String text, Color baseColor) {
            super(text);
            this.currentColor = baseColor;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
    public static class User {
        private int id;
        private String name, email, phone, address, gender, role, status, joinDate; // Added address

        public User() {}

        public User(int id, String name, String email, String phone, String address, String gender, String role, String status, String joinDate) {
            this.id = id; this.name = name; this.email = email; this.phone = phone;
            this.address = address; // Set address
            this.gender = gender; this.role = role; this.status = status; this.joinDate = joinDate;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; } // Getter for address
        public void setAddress(String address) { this.address = address; } // Setter for address
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
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
