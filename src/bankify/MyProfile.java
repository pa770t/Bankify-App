package bankify;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class MyProfile extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;
    // Removed textField_2 from here to prevent NullPointerException
    private JTextField textField, textField_1, textField_3, textField_4, textField_5;
    // Date picker
    private DatePicker datePicker;
    // Error Labels
    private JLabel err1, err2, err3, err4, err5, err6;

    public MyProfile() {
        setTitle("Bankify - My Profile");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        Sidebar sidebar = new Sidebar(this, "Settings");
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
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

        textField_5 = new JTextField();
        textField_5.setFont(fieldFont);
        textField_5.setBounds(column2X, 480, fieldWidth, fieldHeight);
        contentPanel.add(textField_5);

        err6 = new JLabel("");
        err6.setForeground(errorColor);
        err6.setFont(errorFont);
        err6.setBounds(column2X, 525, fieldWidth, 25);
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
        textField.setEditable(true);
        textField_1.setEditable(true);
        datePicker.setEnabled(true); // Fixed: Replaced textField_2
        textField_3.setEditable(true);
        textField_4.setEditable(true);
        textField_5.setEditable(true);
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

        if (textField.getText().trim().isEmpty()) { err1.setText("! First name required"); hasError = true; }
        if (textField_1.getText().trim().isEmpty()) { err2.setText("! Last name required"); hasError = true; }

        // Fixed: Check datePicker instead of textField_2
        if (datePicker.getDate() == null) {
            err3.setText("! Date of birth required");
            hasError = true;
        }

        if (textField_3.getText().trim().isEmpty()) { err4.setText("! Address required"); hasError = true; }

        String email = textField_4.getText().trim();
        if (email.isEmpty()) {
            err5.setText("! Email is required");
            hasError = true;
        } else if (!isValidEmail(email)) {
            err5.setText("! Invalid Gmail format");
            hasError = true;
        }

        String phone = textField_5.getText().trim();
        if (phone.isEmpty()) {
            err6.setText("! Phone required");
            hasError = true;
        } else if (!isValidPhoneNumber(phone)) {
            err6.setText("! Invalid (10-15 digits)");
            hasError = true;
        }

        if (!hasError) {
            JOptionPane.showMessageDialog(this, "Profile saved successfully!\nDOB: " + datePicker.getDate());
            disableTextFields();
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@gmail\\.com$").matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 10 && digits.length() <= 15;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyProfile().setVisible(true));
    }
}