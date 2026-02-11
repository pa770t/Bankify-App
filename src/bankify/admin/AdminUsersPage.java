package bankify.admin;

import bankify.Account;
import bankify.Agent;
import bankify.Customer;
import bankify.dao.AccountDao;
import bankify.dao.AdminDao;
import bankify.dao.AgentDao;
import bankify.dao.CustomerDao;
import bankify.service.EmailService;
import bankify.service.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
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

    public AdminUsersPage(Connection connection) {
        conn = connection;
        customerDao = new CustomerDao(conn);
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
        JButton editButton = createButton("Edit User", new Color(52, 152, 219));
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

        usersTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editUser();
                }
            }
        });

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

        try {
            AccountDao accountDao = new AccountDao(conn);
            AgentDao agentDao1 = new AgentDao(conn);
            Agent agent;

            for (User user : users) {
                Account account = accountDao.getAccountByCustomerId(user.getId());
                agent = agentDao1.findByEmail(user.getEmail());

                String displayRole = "Customer";
                String displayStatus = "Inactive";

                if (agent != null) {
                    // Logic for Employees (Admin/Agent)
                    if ("SYSTEM".equalsIgnoreCase(agent.getRole())) {
                        displayRole = "Admin";
                    } else {
                        displayRole = "Agent";
                    }

                    // Logic for Employee Status
                    if (agent.getStatus() != null) {
                        displayStatus = agent.getStatus();
                    } else {
                        displayStatus = "Active";
                    }

                } else {
                    // Logic for Customers
                    displayRole = "Customer";
                    if (account != null) {
                        displayStatus = "Active";
                    }
                }

                // IMPORTANT: Update the User object with calculated values
                // This ensures searchUsers() uses the correct display data
                user.setRole(displayRole);
                user.setStatus(displayStatus);

                Object[] rowData = {
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        displayRole,
                        displayStatus,
                        user.getJoinDate()
                };
                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void searchUsers() {
        String searchText = searchField.getText().toLowerCase().trim();

        // If search is empty, reload the full table
        if (searchText.isEmpty()) {
            populateTable();
            return;
        }

        tableModel.setRowCount(0);

        for (User user : users) {
            // Null-safe checks
            String id = String.valueOf(user.getId());
            String name = user.getName() != null ? user.getName().toLowerCase() : "";
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
            String phone = user.getPhone() != null ? user.getPhone() : "";

            // Check if search text matches ID, Name, Email, or Phone
            if (id.equals(searchText) ||
                    name.contains(searchText) ||
                    email.contains(searchText) ||
                    phone.contains(searchText)) {

                Object[] rowData = {
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),   // Uses value set in populateTable
                        user.getStatus(), // Uses value set in populateTable
                        user.getJoinDate()
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
            String userRole = user.getRole();

            if ("All Users".equals(filter)) {
                matches = true;
            } else if (filter.equalsIgnoreCase(user.getStatus())) {
                matches = true;
            } else if (filter.equalsIgnoreCase(userRole)) {
                matches = true;
            }

            if (matches) {
                Object[] rowData = {
                        user.getId(), user.getName(), user.getEmail(), user.getPhone(),
                        user.getRole(), user.getStatus(), user.getJoinDate()
                };
                tableModel.addRow(rowData);
            }
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

        if (userToEdit == null) {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDialog dialog = new UserDialog(this, "Edit User", userToEdit);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            User updatedUser = dialog.getUser();

            try {
                if ("Customer".equalsIgnoreCase(updatedUser.getRole())) {
                    Customer customer = new Customer();
                    customer.setCustomerId(updatedUser.getId());

                    String[] nameParts = updatedUser.getName().split(" ", 2);
                    customer.setFirstName(nameParts[0]);
                    customer.setLastName(nameParts.length > 1 ? nameParts[1] : "");

                    customer.setEmail(updatedUser.getEmail());
                    customer.setPhoneNumber(updatedUser.getPhone());
                    customer.setAddress(updatedUser.getAddress());

                    adminDao.updateCustomer(customer);

                } else {
                    Agent agent = new Agent();
                    agent.setAgentId(updatedUser.getId());
                    agent.setFullName(updatedUser.getName());
                    agent.setEmail(updatedUser.getEmail());
                    agent.setPhoneNumber(updatedUser.getPhone());
                    agent.setAddress(updatedUser.getAddress());
                    agent.setGender(updatedUser.getGender());

                    if ("Admin".equalsIgnoreCase(updatedUser.getRole())) {
                        agent.setRole("SYSTEM");
                    } else {
                        agent.setRole("STAFF");
                    }

                    adminDao.updateAgent(agent);
                }

                int index = users.indexOf(userToEdit);
                if (index != -1) {
                    users.set(index, updatedUser);
                }
                populateTable();
                JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(this, "Add New User", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            User newUser = dialog.getUser();
            EmailService emailService = new EmailService();

            if ("Customer".equalsIgnoreCase(newUser.getRole())) {
                Customer dbCustomer = new Customer();
                String[] nameParts = newUser.getName().split(" ", 2);
                dbCustomer.setFirstName(nameParts[0]);
                dbCustomer.setLastName(nameParts.length > 1 ? nameParts[1] : "");

                dbCustomer.setEmail(newUser.getEmail());
                // Handle potentially null phone if hidden
                dbCustomer.setPhoneNumber(newUser.getPhone().isEmpty() ? null : newUser.getPhone());
                dbCustomer.setAddress(newUser.getAddress());

                String plainPassword = PasswordService.generateRandomPassword(10);
                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
                dbCustomer.setPassword(hashedPassword);
                dbCustomer.setFirstTimeLogin(true);

                Customer createdCustomer = adminDao.createCustomerByAdmin(dbCustomer);
                if (createdCustomer != null) {
                    emailService.sendCustomerCreatedEmail(createdCustomer, plainPassword);
                    newUser.setId(createdCustomer.getCustomerId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save Customer!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                Agent agent = new Agent();
                agent.setFullName(newUser.getName());
                agent.setEmail(newUser.getEmail());
                agent.setGender(newUser.getGender());
                agent.setPhoneNumber(newUser.getPhone());
                agent.setAddress(newUser.getAddress());

                if ("Admin".equalsIgnoreCase(newUser.getRole())) {
                    agent.setRole("SYSTEM");
                } else {
                    agent.setRole("STAFF");
                }

                String plainPassword = PasswordService.generateRandomPassword(10);
                agent.setPassword(plainPassword);

                Agent createdAgent = adminDao.createAgent(agent.getFullName(), agent.getRole(), agent.getGender(),
                        agent.getEmail(), agent.getPhoneNumber(), agent.getAddress(), agent.getPassword());
                if (createdAgent != null) {
                    emailService.sendAgentCreatedEmail(createdAgent, plainPassword);
                    newUser.setId(createdAgent.getAgentId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save Agent!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            users.add(0, newUser);
            populateTable();
            JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            Customer customer = customerDao.findById(userId);
            if (customer != null) {
                adminDao.deleteCustomer(customer.getCustomerId());
            } else {
                adminDao.deleteAgent(userId);
            }
            populateTable();
            JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initializeSampleData() {
        // 1. Fetch the raw list of users from the DAO
        this.users = adminDao.getAllUsers();

        // 2. Enrich each user object with the correct Display Role and Status
        try {
            AccountDao accountDao = new AccountDao(conn);
            AgentDao agentDao = new AgentDao(conn);

            for (User user : this.users) {
                // Check if the user exists in the Agent table (Employee)
                Agent agent = agentDao.findByEmail(user.getEmail());

                String displayRole;
                String displayStatus;

                if (agent != null) {
                    // --- It is an Employee (Agent or Admin) ---

                    // Map DB Role to UI Role
                    if ("SYSTEM".equalsIgnoreCase(agent.getRole())) {
                        displayRole = "Admin";
                    } else {
                        displayRole = "Agent";
                    }

                    // Map DB Status to UI Status
                    String dbStatus = agent.getStatus();
                    if (dbStatus != null && "SUSPENDED".equalsIgnoreCase(dbStatus)) {
                        displayStatus = "Inactive";
                    } else {
                        displayStatus = "Active"; // Default to Active for employees
                    }

                } else {
                    // --- It is a Customer ---
                    displayRole = "Customer";

                    // Check Account table to determine Status
                    Account account = accountDao.getAccountByCustomerId(user.getId());

                    // If account exists and isn't closed, they are Active
                    if (account != null && !"CLOSED".equalsIgnoreCase(account.getStatus())) {
                        displayStatus = "Active";
                    } else {
                        displayStatus = "Inactive";
                    }
                }

                // 3. Save the calculated values into the User object
                user.setRole(displayRole);
                user.setStatus(displayStatus);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Tw Cen MT", Font.PLAIN, 14));

            String status = String.valueOf(value);
            Color bgColor = "Active".equalsIgnoreCase(status) ? new Color(39, 174, 96) : new Color(231, 76, 60);
            cell.setBackground(bgColor);
            cell.setForeground(Color.WHITE);
            if (cell instanceof JLabel) ((JLabel) cell).setOpaque(true);
            return cell;
        }
    }

    class UserDialog extends JDialog {
        private boolean confirmed = false;
        private boolean isEditMode = false; // Flag to track mode
        private User user;

        private JTextField firstNameField, lastNameField, fullNameField, emailField, phoneField, addressField;
        private JLabel lblFirstName, lblLastName, lblFullName, lblPhone;
        private JComboBox<String> genderCombo, roleCombo, statusCombo;
        private JPanel formPanel;

        public UserDialog(JFrame parent, String title, User existingUser) {
            super(parent, title, true);
            this.isEditMode = (existingUser != null); // Set the flag

            setSize(520, 600);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            formPanel.setBackground(Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 10, 8, 10);
            gbc.gridx = 0; gbc.gridy = 0; gbc.ipady = 10;

            // 1. Name Fields
            gbc.gridx = 0; gbc.gridy = 0;
            lblFirstName = new JLabel("First Name:");
            formPanel.add(lblFirstName, gbc);
            gbc.gridx = 1;
            firstNameField = new JTextField(20);
            formPanel.add(firstNameField, gbc);

            gbc.gridx = 0; gbc.gridy++;
            lblLastName = new JLabel("Last Name:");
            formPanel.add(lblLastName, gbc);
            gbc.gridx = 1;
            lastNameField = new JTextField(20);
            formPanel.add(lastNameField, gbc);

            gbc.gridx = 0; gbc.gridy++;
            lblFullName = new JLabel("Full Name:");
            formPanel.add(lblFullName, gbc);
            gbc.gridx = 1;
            fullNameField = new JTextField(20);
            formPanel.add(fullNameField, gbc);

            // 2. Contact Details
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            emailField = new JTextField(20);
            formPanel.add(emailField, gbc);

            // Phone Field with Label Variable
            gbc.gridx = 0; gbc.gridy++;
            lblPhone = new JLabel("Phone:");
            formPanel.add(lblPhone, gbc);
            gbc.gridx = 1;
            phoneField = new JTextField(20);
            formPanel.add(phoneField, gbc);

            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1;
            addressField = new JTextField(20);
            formPanel.add(addressField, gbc);

            // 3. Gender
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Gender:"), gbc);
            gbc.gridx = 1;
            genderCombo = new JComboBox<>(new String[]{"MALE", "FEMALE", "LGBTQ+"});
            formPanel.add(genderCombo, gbc);

            // 4. Role
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Role:"), gbc);
            gbc.gridx = 1;
            roleCombo = new JComboBox<>(new String[]{"Customer", "Agent", "Admin"});
            formPanel.add(roleCombo, gbc);

            // 5. Status
            gbc.gridx = 0; gbc.gridy++;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
            formPanel.add(statusCombo, gbc);

            roleCombo.addActionListener(e -> updateFieldVisibility((String) roleCombo.getSelectedItem()));

            // Populate data if editing
            if (existingUser != null) {
                user = existingUser;
                if ("Customer".equalsIgnoreCase(user.getRole())) {
                    String[] nameParts = user.getName().split(" ", 2);
                    firstNameField.setText(nameParts[0]);
                    lastNameField.setText(nameParts.length > 1 ? nameParts[1] : "");
                } else {
                    fullNameField.setText(user.getName());
                }
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhone());
                addressField.setText(user.getAddress());
                genderCombo.setSelectedItem(user.getGender());
                statusCombo.setSelectedItem(user.getStatus());

                roleCombo.setSelectedItem(user.getRole());
            } else {
                user = new User();
                updateFieldVisibility("Customer");
            }

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            JButton saveButton = new RoundedButton2("Save", new Color(39, 174, 96));
            saveButton.addActionListener(e -> {
                try {
                    if (!validateForm()) return;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                confirmed = true;

                String role = (String) roleCombo.getSelectedItem();
                String finalName = "";

                if ("Customer".equalsIgnoreCase(role)) {
                    finalName = firstNameField.getText().trim() + " " + lastNameField.getText().trim();
                } else {
                    finalName = fullNameField.getText().trim();
                }

                user.setName(finalName);
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim()); // Will be empty string if hidden
                user.setAddress(addressField.getText().trim());
                user.setGender((String) genderCombo.getSelectedItem());
                user.setRole(role);
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

            updateFieldVisibility((String) roleCombo.getSelectedItem());
        }

        private void updateFieldVisibility(String role) {
            boolean isCustomer = "Customer".equalsIgnoreCase(role);

            // Name Fields Logic
            lblFirstName.setVisible(isCustomer);
            firstNameField.setVisible(isCustomer);
            lblLastName.setVisible(isCustomer);
            lastNameField.setVisible(isCustomer);

            lblFullName.setVisible(!isCustomer);
            fullNameField.setVisible(!isCustomer);

            // Phone Field Logic: Hide only if adding a Customer
            if (isCustomer && !isEditMode) {
                lblPhone.setVisible(false);
                phoneField.setVisible(false);
            } else {
                lblPhone.setVisible(true);
                phoneField.setVisible(true);
            }
        }

        private boolean validateForm() throws SQLException {
            String role = (String) roleCombo.getSelectedItem();
            AccountDao accountDao = new AccountDao(conn);
            if ("Customer".equalsIgnoreCase(role)) {
                if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
                    showError("First Name and Last Name are required.");
                    return false;
                }
            } else {
                if (fullNameField.getText().trim().isEmpty()) {
                    showError("Full Name is required.");
                    return false;
                }
            }

            if (emailField.getText().trim().isEmpty() || !emailField.getText().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                showError("Please enter a valid email address.");
                return false;
            } else if (!isEditMode) {
                boolean isEmailUsed = adminDao.hasEmailForBoth(emailField.getText().trim());
                if (isEmailUsed) {
                    showError("Email is already used!");
                    return false;
                }
            }

            // Only validate phone if it is visible
            if (phoneField.isVisible()) {
                if (phoneField.getText().trim().isEmpty() || !phoneField.getText().trim().matches("\\d{10}")) {
                    showError("Phone number must contain exactly 10 digits. (9*********)");
                    return false;
                } else if (!isEditMode) {
                    // Check if phone number is already used by ANOTHER user
                    boolean isPhoneUsed = adminDao.hasPhoneForBoth(phoneField.getText().trim());
                    if (isPhoneUsed) {
                        showError("Phone number is already used!");
                        return false;
                    }
                }
            }

            if (addressField.getText().trim().isEmpty()) {
                showError("Address is required.");
                return false;
            }
            return true;
        }

        private void showError(String msg) {
            JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
        }

        public String getFirstName() { return firstNameField.getText().trim(); }
        public String getLastName() { return lastNameField.getText().trim(); }
        public String getEmail() { return emailField.getText().trim(); }
        public String getPhone() { return phoneField.getText().trim(); }
        public String getAddress() { return addressField.getText().trim(); }
        public boolean isConfirmed() { return confirmed; }
        public User getUser() { return user; }
    }

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

    public static class User {
        private int id;
        private String name, email, phone, address, gender, role, status, joinDate;
        public User() {}
        public User(int id, String name, String email, String phone, String address, String gender, String role, String status, String joinDate) {
            this.id = id; this.name = name; this.email = email; this.phone = phone;
            this.address = address; this.gender = gender; this.role = role; this.status = status; this.joinDate = joinDate;
        }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
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
            AdminUsersPage frame = new AdminUsersPage(conn);
            frame.setVisible(true);
        });
    }
}