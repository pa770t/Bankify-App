package bankify;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.text.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.regex.Pattern;
import bankify.dao.*;

public class MyProfile extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;
    // Removed textField_2 from here to prevent NullPointerException
    private JTextField textField, textField_1, textField_3, textField_4, textField_5;
    // Date picker
    private DatePicker datePicker;
    // Error Labels
    private JLabel err1, err2, err3, err4, err5, err6;
    // auto pass firstname lastname and email
    private Customer customer;
    private CustomerDao customerDao;


    public MyProfile() {
        setTitle("Bankify - My Profile");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        Sidebar sidebar = new Sidebar(this, "Settings",customer, customerDao);
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

 // New constructor for first-time login profile setup
    public MyProfile(Customer customer, CustomerDao customerDao) {
        this(); // call default constructor to build UI
        this.customer = customer;
        this.customerDao = customerDao;

        textField.setText(customer.getFirstName());
        textField.setEditable(false);

        textField_1.setText(customer.getLastName());
        textField_1.setEditable(false);

        textField_4.setText(customer.getEmail());
        textField_4.setEditable(false);

        if (customer.getPassword() != null) {
            textField_3.setText(customer.getAddress());
        }

        if (customer.getPhoneNumber() != null) {
            textField_5.setText(customer.getPhoneNumber());
        }
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30,127,179));
        contentPanel.setLayout(null);

        JPanel settingsHeaderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 70, 70);
                g2.dispose();
            }
        };
        settingsHeaderPanel.setLayout(null);
        settingsHeaderPanel.setBackground(new Color(0, 191, 255));
        settingsHeaderPanel.setBounds(340, 60, 220, 70);

        JLabel settingsIconLabel = new JLabel("");
        URL settingsIconURL = getClass().getResource("/Resources/my_profile.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image scaledImage = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            settingsIconLabel.setIcon(new ImageIcon(scaledImage));
        }
        settingsIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        settingsIconLabel.setBounds(15, 12, 55, 45);
        settingsHeaderPanel.add(settingsIconLabel);

        JLabel settingsLabel = new JLabel("My Profile");
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        settingsLabel.setBounds(75, 12, 150, 45);
        settingsHeaderPanel.add(settingsLabel);

        contentPanel.add(settingsHeaderPanel);

        Font labelFont = new Font("Tw Cen MT", Font.BOLD, 18);
        Font fieldFont = new Font("Tw Cen MT", Font.PLAIN, 18);
        Font errorFont = new Font("Tw Cen MT", Font.BOLD, 16);
        Color errorColor = Color.RED;

        int fieldWidth = 370;
        int fieldHeight = 45;
        int column1X = 60;
        int column2X = 460;

        // --- Row 1 ---
        JLabel lblFirstName = new JLabel("First Name");
        lblFirstName.setForeground(Color.WHITE);
        lblFirstName.setFont(labelFont);
        lblFirstName.setBounds(column1X, 200, 150, 30);
        contentPanel.add(lblFirstName);

        textField = new JTextField();
        textField.setFont(fieldFont);
        textField.setBounds(column1X, 240, fieldWidth, fieldHeight);
        contentPanel.add(textField);
        

        err1 = new JLabel("");
        err1.setForeground(errorColor);
        err1.setFont(errorFont);
        err1.setBounds(column1X, 285, fieldWidth, 25);
        contentPanel.add(err1);

        JLabel lblLastName = new JLabel("Last Name");
        lblLastName.setFont(labelFont);
        lblLastName.setForeground(Color.WHITE);
        lblLastName.setBounds(column2X, 200, 150, 30);
        contentPanel.add(lblLastName);

        textField_1 = new JTextField();
        textField_1.setFont(fieldFont);
        textField_1.setBounds(column2X, 240, fieldWidth, fieldHeight);
        contentPanel.add(textField_1);

        err2 = new JLabel("");
        err2.setForeground(errorColor);
        err2.setFont(errorFont);
        err2.setBounds(column2X, 285, fieldWidth, 25);
        contentPanel.add(err2);

        // --- Row 2 (DOB / Address) ---
        JLabel lblDob = new JLabel("Date of Birth");
        lblDob.setForeground(Color.WHITE);
        lblDob.setFont(labelFont);
        lblDob.setBounds(column1X, 320, 150, 30);
        contentPanel.add(lblDob);

        // DATE PICKER IMPLEMENTATION
        Font date_font = new Font("Tw Cen MT", Font.PLAIN, 18);
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dateSettings.setAllowEmptyDates(false);
        dateSettings.setFontValidDate(date_font);
        dateSettings.setFontInvalidDate(date_font);

        datePicker = new DatePicker(dateSettings);
        dateSettings.setDateRangeLimits(null, LocalDate.now());

        datePicker.setBounds(column1X, 360, fieldWidth, fieldHeight);
        datePicker.getComponentDateTextField().setFont(date_font);

        URL dateIconURL = getClass().getResource("/Resources/calendar.png");
        if (dateIconURL != null) {
            ImageIcon dateIcon = new ImageIcon(dateIconURL);
            Image scaledDateIcon = dateIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            datePicker.getComponentToggleCalendarButton().setIcon(new ImageIcon(scaledDateIcon));
            datePicker.getComponentToggleCalendarButton().setText("");
        }
        contentPanel.add(datePicker);

        err3 = new JLabel("");
        err3.setForeground(errorColor);
        err3.setFont(errorFont);
        err3.setBounds(column1X, 405, fieldWidth, 25);
        contentPanel.add(err3);

        JLabel lblAddress = new JLabel("Address");
        lblAddress.setForeground(Color.WHITE);
        lblAddress.setFont(labelFont);
        lblAddress.setBounds(column2X, 320, 150, 30);
        contentPanel.add(lblAddress);

        textField_3 = new JTextField();
        textField_3.setFont(fieldFont);
        textField_3.setBounds(column2X, 360, fieldWidth, fieldHeight);
        contentPanel.add(textField_3);

        err4 = new JLabel("");
        err4.setForeground(errorColor);
        err4.setFont(errorFont);
        err4.setBounds(column2X, 405, fieldWidth, 25);
        contentPanel.add(err4);

        // --- Row 3 ---
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(Color.WHITE);
        lblEmail.setBounds(column1X, 440, 150, 30);
        contentPanel.add(lblEmail);

        textField_4 = new JTextField();
        textField_4.setFont(fieldFont);
        textField_4.setBounds(column1X, 480, fieldWidth, fieldHeight);
        contentPanel.add(textField_4);

        err5 = new JLabel("");
        err5.setForeground(errorColor);
        err5.setFont(errorFont);
        err5.setBounds(column1X, 525, fieldWidth, 25);
        contentPanel.add(err5);

        JLabel lblPhone = new JLabel("Phone Number");
        lblPhone.setFont(labelFont);
        lblPhone.setForeground(Color.WHITE);
        lblPhone.setBounds(column2X, 440, 150, 30);
        contentPanel.add(lblPhone);
        
        JPanel phoneContainer = new JPanel();
        phoneContainer.setBounds(column2X, 480, fieldWidth, fieldHeight);
        phoneContainer.setLayout(new BorderLayout());
        phoneContainer.setBackground(contentPanel.getBackground());

        JPanel countryCodePanel = new JPanel();
        countryCodePanel.setLayout(new BorderLayout());
        countryCodePanel.setPreferredSize(new Dimension(60, fieldHeight));
        countryCodePanel.setBackground(new Color(245, 245, 245));
        countryCodePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));

        JLabel countryCodeLabel = new JLabel("+95");
        countryCodeLabel.setFont(new Font(fieldFont.getName(), Font.BOLD, fieldFont.getSize()));
        countryCodeLabel.setForeground(Color.BLACK);
        countryCodePanel.add(countryCodeLabel, BorderLayout.WEST);

        // Phone number text field
        textField_5 = new JTextField();
        textField_5.setFont(fieldFont);
        textField_5.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));
     
        textField_5.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                JTextField field = (JTextField) e.getSource();
                char c = e.getKeyChar();
                
                
                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }
                
               
                if (field.getText().length() >= 10) {
                    e.consume();
                }
            }
        });
       
        textField_5.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                JTextField field = (JTextField) e.getSource();
                String text = field.getText();
                
               
                String digitsOnly = text.replaceAll("[^0-9]", "");
                
                
                if (digitsOnly.length() > 10) {
                    digitsOnly = digitsOnly.substring(0, 10);
                }
                
                
                if (!text.equals(digitsOnly)) {
                    field.setText(digitsOnly);
                }
            }
        });

        phoneContainer.add(countryCodePanel, BorderLayout.WEST);
        phoneContainer.add(textField_5, BorderLayout.CENTER);
        contentPanel.add(phoneContainer);

        err6 = new JLabel("");
        err6.setForeground(errorColor);
        err6.setFont(errorFont);
        err6.setBounds(column1X, 525, fieldWidth, 25);
        contentPanel.add(err6);

        // --- Buttons ---
        JButton btnEdit = new RoundedCornerButton("Edit");
        btnEdit.setBounds(column2X + 130, 580, 100, 55);
        btnEdit.addActionListener(e -> enableEditing());
        contentPanel.add(btnEdit);

        JButton btnSave = new RoundedCornerButton("Save");
        btnSave.setBounds(column2X + 250, 580, 100, 55);
        btnSave.addActionListener(e -> saveProfile());
        contentPanel.add(btnSave);

        disableTextFields();
        return contentPanel;
    }
    private void enableEditing() {
        // Keep first name, last name, and email locked
        textField.setEditable(false);   // First name
        textField_1.setEditable(false); // Last name
        textField_4.setEditable(false); // Email

        // Allow editing only for DOB, address, and phone
        datePicker.setEnabled(true);
        textField_3.setEditable(true);  // Address
        textField_5.setEditable(true);  // Phone

        clearErrors();
    }

    private void disableTextFields() {
        textField.setEditable(false);
        textField_1.setEditable(false);
        if(datePicker != null) datePicker.setEnabled(false); // Fixed: Replaced textField_2
        textField_3.setEditable(false);
        textField_4.setEditable(false);
        textField_5.setEditable(false);
    }

    private void clearErrors() {
        err1.setText(""); err2.setText(""); err3.setText("");
        err4.setText(""); err5.setText(""); err6.setText("");
    }

    private void saveProfile() {
        clearErrors();
        boolean hasError = false;

        // validate DOB, address, phone only
        if (datePicker.getDate() == null) { err3.setText("! Date of birth required"); hasError = true; }
        if (textField_3.getText().trim().isEmpty()) { err4.setText("! Address required"); hasError = true; }
        String phone = textField_5.getText().trim();
        if (phone.isEmpty()) { err6.setText("! Phone required"); hasError = true; }
        else if (!isValidPhoneNumber(phone)) { err6.setText("! Invalid (10-15 digits)"); hasError = true; }

        if (!hasError) {
            // Update only editable fields
            customer.setPhoneNumber(phone);
            customer.setAddress(textField_3.getText());
            customer.setDob(datePicker.getDate());
            customer.setFirstTimeLogin(false);

            boolean success = customerDao.updateProfile(customer);
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile saved successfully!");
                disableTextFields();
                AccountDao accountDao = new AccountDao(DBConnection.getConnection());
                if (customer.isFirstTimeLogin()) {
                accountDao.createAccount(customer);
                }
                dispose(); // close profile window
                new HomePage(customer,customerDao).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Failed to save profile. Please try again.");
            }
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@gmail\\.com$").matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() == 10 ;
    }

    private class RoundedCornerButton extends JButton {
        private Color baseColor;
        public RoundedCornerButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            baseColor = text.equals("Edit") ? new Color(220, 20, 60) : new Color(50, 205, 50);
            setBackground(baseColor);
            setForeground(Color.WHITE);
            setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 55, 55);
            g2.dispose();
            super.paintComponent(g);
        }
    }

  

}
