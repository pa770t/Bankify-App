package bankify;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.border.EmptyBorder;

import bankify.service.AuthService;
import bankify.dao.CustomerDao;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtUserName;
    private JPasswordField pwtPassword;
    private boolean showPassword = false;

    // Error Labels
    private JLabel errEmail, errPass;

    public Login() {
        DBConnection.getConnection();
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(null);

        // ===== Email Label =====
        JLabel lblUserName = new JLabel("Email");
        lblUserName.setFont(new Font("Tw Cen MT", Font.PLAIN, 24));
        lblUserName.setBounds(70, 129, 150, 30);
        leftPanel.add(lblUserName);

        // ===== Email Field =====
        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBounds(70, 170, 300, 45);
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

        txtUserName = new JTextField();
        txtUserName.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        txtUserName.setBorder(new EmptyBorder(2, 5, 2, 5));
        txtUserName.setForeground(Color.BLACK);
        txtUserName.setOpaque(false);
        usernamePanel.add(txtUserName, BorderLayout.CENTER);
        leftPanel.add(usernamePanel);

        // Inline Email Error Label
        errEmail = new JLabel("");
        errEmail.setForeground(Color.RED);
        errEmail.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        errEmail.setBounds(70, 220, 300, 20);
        leftPanel.add(errEmail);

        txtUserName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                usernamePanel.setBorder(BorderFactory.createLineBorder(new Color(30, 127, 179), 2, true));
            }

            @Override
            public void focusLost(FocusEvent e) {
                usernamePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
            }
        });

        // ===== Password Label =====
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tw Cen MT", Font.PLAIN, 24));
        lblPassword.setBounds(70, 251, 150, 30);
        leftPanel.add(lblPassword);

        // ===== Password Field + Eye Icon =====
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBounds(70, 292, 300, 45);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

        pwtPassword = new JPasswordField();
        pwtPassword.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        pwtPassword.setEchoChar('•');
        pwtPassword.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));
        pwtPassword.setForeground(Color.BLACK);
        pwtPassword.setOpaque(false);
        passwordPanel.add(pwtPassword, BorderLayout.CENTER);

        ImageIcon hideIcon = new ImageIcon(getClass().getResource("/Resources/hide.png"));
        JLabel eyeIcon1 = new JLabel(hideIcon);
        eyeIcon1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        passwordPanel.add(eyeIcon1, BorderLayout.EAST);

        eyeIcon1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        leftPanel.add(passwordPanel);

        // Inline Password Error Label
        errPass = new JLabel("");
        errPass.setForeground(Color.RED);
        errPass.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        errPass.setBounds(70, 342, 300, 20);
        leftPanel.add(errPass);

        eyeIcon1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPassword = !showPassword;
                if (showPassword) {
                    pwtPassword.setEchoChar((char) 0);
                    eyeIcon1.setIcon(new ImageIcon(getClass().getResource("/Resources/eye.png")));
                } else {
                    pwtPassword.setEchoChar('•');
                    eyeIcon1.setIcon(new ImageIcon(getClass().getResource("/Resources/hide.png")));
                }
            }
        });

        pwtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(30, 127, 179), 2, true));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
            }
        });

        // ===== Login Button =====
        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(30, 127, 179));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        btnLogin.setBounds(70, 390, 300, 45);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        leftPanel.add(btnLogin);

        JLabel lblNoAccount = new JLabel("Don't have an account?");
        lblNoAccount.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        lblNoAccount.setBounds(70, 457, 200, 25);
        leftPanel.add(lblNoAccount);

        JButton btnSignUp = new JButton("Register");
        btnSignUp.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        btnSignUp.setForeground(new Color(30, 127, 179));
        btnSignUp.setBackground(Color.WHITE);
        btnSignUp.setBorderPainted(false);
        btnSignUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSignUp.setBounds(270, 457, 120, 25);
        leftPanel.add(btnSignUp);

        // ===== Right Panel =====
        JPanel rightPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Polygon p = new Polygon();
                p.addPoint(0, 0);
                p.addPoint(width, (int) (height * 1));
                p.addPoint(width, height);
                p.addPoint(0, height);
                g2.setColor(Color.WHITE);
                g2.fillPolygon(p);
            }
        };
        rightPanel.setBackground(new Color(30, 127, 179));
        rightPanel.setLayout(null);

        JLabel lblWelcome = new JLabel("WELCOME TO");
        lblWelcome.setForeground(Color.BLACK);
        lblWelcome.setFont(new Font("Tw Cen MT", Font.BOLD, 51));
        lblWelcome.setBounds(166, 132, 301, 41);
        rightPanel.add(lblWelcome);

        JLabel lblNewLabel = new JLabel("BANKIFY !");
        lblNewLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 51));
        lblNewLabel.setForeground(Color.BLACK);
        lblNewLabel.setBounds(230, 210, 270, 41);
        rightPanel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("\"Fast.Safe.Reliable.");
        lblNewLabel_1.setFont(new Font("Tw Cen MT", Font.PLAIN, 25));
        lblNewLabel_1.setForeground(Color.BLACK);
        lblNewLabel_1.setBounds(262, 278, 270, 30);
        rightPanel.add(lblNewLabel_1);

        JLabel lblNewLabel_3 = new JLabel("Your Bank,Your ");
        lblNewLabel_3.setFont(new Font("Tw Cen MT", Font.PLAIN, 26));
        lblNewLabel_3.setForeground(Color.BLACK);
        lblNewLabel_3.setBounds(299, 337, 270, 18);
        rightPanel.add(lblNewLabel_3);

        JLabel lblNewLabel_2 = new JLabel("Own Way.\"");
        lblNewLabel_2.setFont(new Font("Tw Cen MT", Font.PLAIN, 23));
        lblNewLabel_2.setForeground(Color.BLACK);
        lblNewLabel_2.setBounds(345, 383, 270, 33);
        rightPanel.add(lblNewLabel_2);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Resources/loginlogo.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(275, 175, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(logoImg));
        lblLogo.setBounds(74, 275, 138, 305);
        rightPanel.add(lblLogo);

        // ===== Combine Panels =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // ===== Login Action =====
        btnLogin.addActionListener(e -> {
            String email = txtUserName.getText().trim();
            String password = new String(pwtPassword.getPassword()).trim();

            CustomerDao dao = new CustomerDao(DBConnection.getConnection());
            AuthService auth = new AuthService(dao);

            // Clear previous errors
            errEmail.setText("");
            errPass.setText("");

            boolean hasError = false;

            if (email.isEmpty()) {
                errEmail.setText("Please enter email!");
                hasError = true;
            }
            if (password.isEmpty()) {
                errPass.setText("Please enter password!");
                hasError = true;
            }

            if (!hasError) {
                Customer customer = null;
                try {
                    customer = auth.authenticate(email, password);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if (customer != null) {
                    dispose();
                    if (customer.isFirstTimeLogin()) {
                        // First-time user → must complete profile
                        new MyProfile(customer, dao).setVisible(true);
                    } else {
                        // Returning user → go straight to homepage
                    	new HomePage(customer, dao).setVisible(true);
                    }
                } else {
                    errPass.setText("Invalid email or password!");
                }
            }
        }); // ✅ close btnLogin lambda properly

        btnSignUp.addActionListener(e -> {
            dispose();
            new Register().setVisible(true);
        });
        
    }
}