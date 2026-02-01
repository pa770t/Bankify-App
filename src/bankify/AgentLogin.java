package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

import bankify.dao.AgentDao;
import bankify.service.AuthService;
import bankify.Customer;
import bankify.dao.CustomerDao;

public class AgentLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtAgentID;
    private JPasswordField pwtPassword;
    private boolean showPassword = false;

    // Error Labels
    private JLabel errEmail, errPass;

    /**
     * Launch the application directly.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AgentLogin frame = new AgentLogin();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public AgentLogin() {
        setTitle("Bankify - Agent Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());

        // =================================================================
        // ===== LEFT PANEL (BRANDING) - Dark Theme for Agents =====
        // =================================================================
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Dark Background (Midnight Blue)
                g2.setColor(new Color(44, 62, 80)); 
                g2.fillRect(0, 0, width, height);
                
                // Decorative Diagonal Shape for modern look
                Polygon p = new Polygon();
                p.addPoint(width, 0);
                p.addPoint(width, height);
                p.addPoint(0, height);
                g2.setColor(new Color(52, 73, 94)); // Slightly lighter shade
                g2.fillPolygon(p);
            }
        };
        leftPanel.setLayout(null);

        // Title: AGENT
        JLabel lblTitle = new JLabel("AGENT");
        lblTitle.setForeground(new Color(236, 240, 241)); // Off White
        lblTitle.setFont(new Font("Tw Cen MT", Font.BOLD, 48));
        lblTitle.setBounds(50, 180, 300, 50);
        leftPanel.add(lblTitle);

        // Title: PORTAL
        JLabel lblTitle2 = new JLabel("PORTAL");
        lblTitle2.setFont(new Font("Tw Cen MT", Font.BOLD, 48));
        lblTitle2.setForeground(new Color(230, 126, 34)); // Orange/Gold Accent
        lblTitle2.setBounds(50, 230, 300, 50);
        leftPanel.add(lblTitle2);

        // Subtitle
        JLabel lblSubtitle = new JLabel("<html>Authorized Personnel<br>Access Only</html>");
        lblSubtitle.setFont(new Font("Tw Cen MT", Font.PLAIN, 22));
        lblSubtitle.setForeground(Color.LIGHT_GRAY);
        lblSubtitle.setBounds(50, 300, 250, 60);
        leftPanel.add(lblSubtitle);


        // =================================================================
        // ===== RIGHT PANEL (INPUTS) =====
        // =================================================================
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);

        JLabel lblLoginTitle = new JLabel("Agent Login");
        lblLoginTitle.setFont(new Font("Tw Cen MT", Font.BOLD, 32));
        lblLoginTitle.setForeground(new Color(44, 62, 80));
        lblLoginTitle.setBounds(70, 80, 250, 40);
        rightPanel.add(lblLoginTitle);

        // ===== Agent ID / Email Label =====
        JLabel lblAgentID = new JLabel("Email");
        lblAgentID.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        lblAgentID.setForeground(Color.DARK_GRAY);
        lblAgentID.setBounds(70, 150, 200, 30);
        rightPanel.add(lblAgentID);

        // ===== Agent ID Field =====
        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setBounds(70, 185, 300, 45);
        idPanel.setBackground(Color.WHITE);
        idPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        txtAgentID = new JTextField();
        txtAgentID.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        txtAgentID.setBorder(new EmptyBorder(2, 10, 2, 5));
        txtAgentID.setForeground(Color.BLACK);
        txtAgentID.setOpaque(false);
        idPanel.add(txtAgentID, BorderLayout.CENTER);
        rightPanel.add(idPanel);

        // Inline Error Label for Email
        errEmail = new JLabel("");
        errEmail.setForeground(new Color(231, 76, 60)); // Red
        errEmail.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        errEmail.setBounds(70, 235, 300, 20);
        rightPanel.add(errEmail);

        // Focus Effect for ID
        txtAgentID.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                idPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 126, 34), 2, true)); // Orange focus
            }
            @Override
            public void focusLost(FocusEvent e) {
                idPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
            }
        });

        // ===== Password Label =====
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Tw Cen MT", Font.PLAIN, 20));
        lblPassword.setForeground(Color.DARK_GRAY);
        lblPassword.setBounds(70, 260, 150, 30);
        rightPanel.add(lblPassword);

        // ===== Password Field =====
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBounds(70, 295, 300, 45);
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        pwtPassword = new JPasswordField();
        pwtPassword.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        pwtPassword.setEchoChar('•');
        pwtPassword.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
        pwtPassword.setForeground(Color.BLACK);
        pwtPassword.setOpaque(false);
        passwordPanel.add(pwtPassword, BorderLayout.CENTER);

        // Eye Icon
        ImageIcon hideIcon = new ImageIcon(getClass().getResource("/Resources/hide.png"));
        JLabel eyeIcon1 = new JLabel(hideIcon);
        eyeIcon1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeIcon1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); 
        passwordPanel.add(eyeIcon1, BorderLayout.EAST);

        rightPanel.add(passwordPanel);

        // Inline Password Error
        errPass = new JLabel("");
        errPass.setForeground(new Color(231, 76, 60)); // Red
        errPass.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        errPass.setBounds(70, 345, 300, 20);
        rightPanel.add(errPass);

        // Toggle Password Logic
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

        // Focus Effect for Password
        pwtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 126, 34), 2, true));
            }
            @Override
            public void focusLost(FocusEvent e) {
                passwordPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
            }
        });

        // ===== Login Button =====
        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(44, 62, 80)); // Dark Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnLogin.setBounds(70, 400, 300, 50);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        rightPanel.add(btnLogin);
        
        // Hover effect for Button
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(230, 126, 34)); // Turn Orange on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(44, 62, 80)); // Back to Dark
            }
        });

        // Back to Customer Login Link
      /*  JLabel lblBack = new JLabel("<html><u>Not an Agent? Login as Customer</u></html>");
        lblBack.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));
        lblBack.setForeground(Color.GRAY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setBounds(110, 480, 250, 20);
        rightPanel.add(lblBack);
        
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new Login().setVisible(true); // Go back to main Customer Login
            }
        }); */

        // ===== Combine Panels (SplitPane) =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400); 
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // ===== Login Action =====
        btnLogin.addActionListener(e -> {
            String email = txtAgentID.getText().trim();
            String password = new String(pwtPassword.getPassword()).trim();
            
            // NOTE: Using CustomerDao for now. 
            // In a real app, use AgentDao/AgentService here.
            CustomerDao dao = new CustomerDao(DBConnection.getConnection());
            AuthService auth = new AuthService(dao);

            errEmail.setText("");
            errPass.setText("");

            boolean hasError = false;

            if (email.isEmpty()) {
                errEmail.setText("Gmail is required!");
                hasError = true;
            }
            if (password.isEmpty()) {
                errPass.setText("Password is required!");
                hasError = true;
            }

            if (!hasError) {
                AgentDao agentDao = new AgentDao(DBConnection.getConnection());
                Agent agent = auth.authenticateAgent(email, password, agentDao);

                if (agent != null) {
                    dispose();
                    // Open Agent Dashboard here
                    JOptionPane.showMessageDialog(null, "Agent Login Successful!");
                    new AgentRequestListPage(agent, agentDao).setVisible(true);
                } else {
                    errPass.setText("Access Denied: Invalid Credentials");
                }
            }
        });
    }
}