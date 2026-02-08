package bankify;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import java.awt.event.*;
import java.sql.Connection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import bankify.service.AuthService;
import bankify.dao.CustomerDao;

public class Register extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Connection conn;
    private JPanel contentPane;
    private JTextField txtFirstName, txtLastName, txtGmail;
    private JPasswordField password, confirmPassword;
    private boolean showPassword = false, showConfirmPassword = false;

    // Error Labels
    private JLabel errFirst, errLast, errEmail, errPass, errConfirm;

    // Style Constants
    private final Color primaryBlue = new Color(30, 127, 179);
    private final Color fieldGray = new Color(128, 128, 128);
    private final Color errorRed = new Color(200, 0, 0);

    // ==================== VALIDATION PATTERNS ====================
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final String NAME_PATTERN = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private static final Pattern namePattern = Pattern.compile(NAME_PATTERN);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Register frame = new Register();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Register() {
        conn = DBConnection.getConnection();

        setTitle("Register - Bankify");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Layout Variables
        int leftPanelWidth = 450;
        int rightPanelWidth = 500;
        int inputWidth = 320;
        int inputHeight = 35;
        int labelX = 90;

        // Static Border Style
        CompoundBorder staticFieldBorder = new CompoundBorder(
                new LineBorder(fieldGray, 1),
                new EmptyBorder(5, 10, 5, 10)
        );
        CompoundBorder staticPasswordBorder = new CompoundBorder(
                new LineBorder(fieldGray, 1),
                new EmptyBorder(5, 10, 5, 40)
        );

        // ---------- Right Panel (Form) ----------
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBounds(450, 0, 500, 561);
        panel.setLayout(null);
        contentPane.add(panel);

        // --- First Name ---
        JLabel lblFirstName = new JLabel("First Name");
        lblFirstName.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        lblFirstName.setBounds(labelX, 20, 150, 25);
        panel.add(lblFirstName);

        txtFirstName = new JTextField();
        txtFirstName.setFont(new Font("Tw Cen MT", Font.PLAIN, 17));
        txtFirstName.setBounds(labelX, 45, inputWidth, inputHeight);
        txtFirstName.setBorder(staticFieldBorder);
        panel.add(txtFirstName);

        errFirst = new JLabel("");
        errFirst.setForeground(errorRed);
        errFirst.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        errFirst.setBounds(labelX, 80, inputWidth, 20);
        panel.add(errFirst);

        // --- Last Name ---
        JLabel lblLastName = new JLabel("Last Name");
        lblLastName.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        lblLastName.setBounds(labelX, 100, 150, 25);
        panel.add(lblLastName);

        txtLastName = new JTextField();
        txtLastName.setFont(new Font("Tw Cen MT", Font.PLAIN, 17));
        txtLastName.setBounds(labelX, 125, inputWidth, inputHeight);
        txtLastName.setBorder(staticFieldBorder);
        panel.add(txtLastName);

        errLast = new JLabel("");
        errLast.setForeground(errorRed);
        errLast.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        errLast.setBounds(labelX, 160, inputWidth, 20);
        panel.add(errLast);

        // --- Email ---
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        lblEmail.setBounds(labelX, 180, 150, 25);
        panel.add(lblEmail);

        txtGmail = new JTextField();
        txtGmail.setFont(new Font("Tw Cen MT", Font.PLAIN, 17));
        txtGmail.setBounds(labelX, 205, inputWidth, inputHeight);
        txtGmail.setBorder(staticFieldBorder);
        panel.add(txtGmail);

        errEmail = new JLabel("");
        errEmail.setForeground(errorRed);
        errEmail.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        errEmail.setBounds(labelX, 240, inputWidth, 20);
        panel.add(errEmail);

        // --- Password ---
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        lblPassword.setBounds(labelX, 260, 150, 25);
        panel.add(lblPassword);

        JLayeredPane passwordPane = new JLayeredPane();
        passwordPane.setBounds(labelX, 285, inputWidth, inputHeight);
        panel.add(passwordPane);

        password = new JPasswordField();
        password.setFont(new Font("Tw Cen MT", Font.PLAIN, 17));
        password.setEchoChar('•');
        password.setBounds(0, 0, inputWidth, inputHeight);
        password.setBorder(staticPasswordBorder);
        passwordPane.add(password, Integer.valueOf(1));

        JLabel eyeIcon1 = new JLabel(new ImageIcon(getClass().getResource("/Resources/hide.png")));
        eyeIcon1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeIcon1.setBounds(inputWidth - 30, 7, 20, 20);
        passwordPane.add(eyeIcon1, Integer.valueOf(2));

        errPass = new JLabel("");
        errPass.setForeground(errorRed);
        errPass.setFont(new Font("Tw Cen MT", Font.PLAIN, 11));
        errPass.setBounds(labelX, 320, inputWidth, 20);
        panel.add(errPass);

        // --- Confirm Password ---
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        lblConfirm.setBounds(labelX, 340, 200, 25);
        panel.add(lblConfirm);

        JLayeredPane confirmPane = new JLayeredPane();
        confirmPane.setBounds(labelX, 365, inputWidth, inputHeight);
        panel.add(confirmPane);

        confirmPassword = new JPasswordField();
        confirmPassword.setFont(new Font("Tw Cen MT", Font.PLAIN, 17));
        confirmPassword.setEchoChar('•');
        confirmPassword.setBounds(0, 0, inputWidth, inputHeight);
        confirmPassword.setBorder(staticPasswordBorder);
        confirmPane.add(confirmPassword, Integer.valueOf(1));

        JLabel eyeIcon2 = new JLabel(new ImageIcon(getClass().getResource("/Resources/hide.png")));
        eyeIcon2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeIcon2.setBounds(inputWidth - 30, 7, 20, 20);
        confirmPane.add(eyeIcon2, Integer.valueOf(2));

        errConfirm = new JLabel("");
        errConfirm.setForeground(errorRed);
        errConfirm.setFont(new Font("Tw Cen MT", Font.PLAIN, 12));
        errConfirm.setBounds(labelX, 400, inputWidth, 20);
        panel.add(errConfirm);

        // --- Register Button ---
        JButton btnRegister = new JButton("Register");
        btnRegister.setBackground(primaryBlue);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        btnRegister.setBounds(90, 445, inputWidth, 45);
        btnRegister.setFocusPainted(false);
        panel.add(btnRegister);

        // ==================== REGISTER BUTTON ACTION ====================
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Clear all labels first
                errFirst.setText(""); errLast.setText(""); errEmail.setText("");
                errPass.setText(""); errConfirm.setText("");

                String firstName = txtFirstName.getText().trim();
                String lastName = txtLastName.getText().trim();
                String gmail = txtGmail.getText().trim();
                String pass = new String(password.getPassword());
                String cpass = new String(confirmPassword.getPassword());

                boolean hasError = false;

                if (firstName.isEmpty()) { errFirst.setText("Please Enter First Name!"); hasError = true; }
                else if (!isValidName(firstName)) { errFirst.setText("Invalid characters!"); hasError = true; }

                if (lastName.isEmpty()) { errLast.setText("Please Enter Last Name!"); hasError = true; }
                else if (!isValidName(lastName)) { errLast.setText("Invalid characters!"); hasError = true; }

                if (gmail.isEmpty()) {
                    errEmail.setText("Please Enter Email!");
                    hasError = true;
                }
                else if (!isValidEmail(gmail)) {
                    errEmail.setText("Invalid email format");
                    hasError = true;
                }
                else if (isValidEmail(gmail)) {
                    CustomerDao customerDao = new CustomerDao(conn);
                    boolean hasEmail = customerDao.hasEmailForBoth(gmail);
                    if (hasEmail) {
                        errEmail.setText("This email is already used!");
                        hasError = true;
                    }
                }

                if (pass.isEmpty()) { errPass.setText("Please Type Password!"); hasError = true; }
                else if (!isValidPassword(pass)) { errPass.setText("8+ chars, Uppercase, Digit, Special (@$!%*?&)"); hasError = true; }

                if (cpass.isEmpty()) { errConfirm.setText("Please Confirm Password!"); hasError = true; }
                else if (!pass.equals(cpass)) { errConfirm.setText("Passwords do not match!"); hasError = true; }

                if (!hasError) {
                    CustomerDao dao = new CustomerDao(conn);
                    AuthService auth = new AuthService(dao, conn);

                    Customer newCustomer = new Customer();
                    newCustomer.setFirstName(firstName);
                    newCustomer.setLastName(lastName);
                    newCustomer.setEmail(gmail);
                    newCustomer.setPassword(pass);
                    newCustomer.setFirstTimeLogin(true); // mark as first-time

                    Customer registeredCustomer = auth.register(newCustomer);

                    if (registeredCustomer != null) {
                        JOptionPane.showMessageDialog(null, "Registration Successful!");
                        dispose();
                        // Pass the customer object into MyProfile so it auto-fills
                        // Get the customer saved in the database
                        new LoadingScreen(() -> {
                            new MyProfile(registeredCustomer, dao, conn).setVisible(true);
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed. Try again.");
                    }
                }
            }
        });

        // ---------- Left Panel (Branding & Flipped Triangle) ----------
        JPanel jframeLeft = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                // Background
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, w, h);

                // Flipped Triangle
                Polygon blueHalf = new Polygon();
                blueHalf.addPoint(0, 0);
                blueHalf.addPoint(w, 0);
                blueHalf.addPoint(0, h);

                g2d.setColor(primaryBlue);
                g2d.fillPolygon(blueHalf);
                g2d.dispose();
            }
        };
        jframeLeft.setBounds(0, 0, 450, 561);
        jframeLeft.setLayout(null);
        contentPane.add(jframeLeft);

        JLabel lblTitle = new JLabel("Welcome to Bankify");
        lblTitle.setFont(new Font("Tw Cen MT", Font.BOLD, 32));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(50, 60, 350, 50);
        jframeLeft.add(lblTitle);

        JLabel lblSub1 = new JLabel("Please register to ");
        lblSub1.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        lblSub1.setForeground(Color.BLACK);
        lblSub1.setBounds(50, 130, 181, 47);
        jframeLeft.add(lblSub1);

        JLabel lblSub2 = new JLabel("continue...");
        lblSub2.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        lblSub2.setForeground(Color.BLACK);
        lblSub2.setBounds(50, 190, 102, 30);
        jframeLeft.add(lblSub2);

        // Image Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Resources/loginlogo.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(290, 200, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(logoImg));
        lblLogo.setBounds(256, 245, 160, 305);
        jframeLeft.add(lblLogo);

        // ==================== EYE ICON LOGIC ====================
        eyeIcon1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPassword = !showPassword;
                password.setEchoChar(showPassword ? (char) 0 : '•');
                eyeIcon1.setIcon(new ImageIcon(getClass().getResource(showPassword ? "/Resources/eye.png" : "/Resources/hide.png")));
            }
        });

        eyeIcon2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showConfirmPassword = !showConfirmPassword;
                confirmPassword.setEchoChar(showConfirmPassword ? (char) 0 : '•');
                eyeIcon2.setIcon(new ImageIcon(getClass().getResource(showConfirmPassword ? "/Resources/eye.png" : "/Resources/hide.png")));
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        Matcher matcher = emailPattern.matcher(email); // Uses the new generic pattern
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) return false;
        Matcher matcher = namePattern.matcher(name);
        return matcher.matches();
    }
}