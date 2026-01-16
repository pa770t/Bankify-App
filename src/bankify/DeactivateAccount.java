package bankify;

import javax.swing.*;



import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class DeactivateAccount extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;

    // Password fields
    private RoundedPasswordField pwtPassword;
 // Error & Success Labels
    private JLabel errPassword;

    public DeactivateAccount() {
        setTitle("Bankify - Deactivate Account");
        // Screen Size ပြောင်းလဲခြင်း
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

     // Sidebar
        Sidebar sidebar = new Sidebar(this, "Settings");


        // Content
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30,127,179));
        contentPanel.setLayout(null);

        // Header Panel
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
        settingsHeaderPanel.setBounds(300, 60, 281, 70);

        JLabel settingsIconLabel = new JLabel("");
        settingsIconLabel.setBounds(20, 15, 40, 40);
        URL settingsIconURL = getClass().getResource("/Resources/deactivate_account.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            settingsIconLabel.setIcon(new ImageIcon(img));
        }
        settingsHeaderPanel.add(settingsIconLabel);

        JLabel settingsLabel = new JLabel("Deactivate Account");
        settingsLabel.setBounds(70, 15, 189, 40);
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Header Font 22
        settingsHeaderPanel.add(settingsLabel);

        contentPanel.add(settingsHeaderPanel);

        

        // Question label
        JLabel lblQuestion = new JLabel("Do you want to deactivate your account?");
        lblQuestion.setOpaque(true);
        lblQuestion.setBackground(new Color(165, 42, 42));
        lblQuestion.setForeground(Color.WHITE);
        lblQuestion.setHorizontalAlignment(SwingConstants.CENTER);
        lblQuestion.setFont(new Font("Tw Cen MT", Font.BOLD, 20)); // Question Font 20
        lblQuestion.setBounds(200, 200, 500, 60); // Size ချိန်ညှိမှု
        contentPanel.add(lblQuestion);

        // Password Field
        pwtPassword = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Password", pwtPassword, 320));
        
        errPassword = new JLabel("");
        errPassword.setBounds(290, 405, 350, 20);
        errPassword.setForeground(Color.RED);
        errPassword.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        contentPanel.add(errPassword);


        // Cancel Button
        RoundedCornerButton btnCancel = new RoundedCornerButton("Cancel", new Color(50, 205, 50));
        btnCancel.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnCancel.setBounds(300, 480, 120, 50);
        btnCancel.addActionListener(e -> {
            dispose();
            new MainSettings().setVisible(true);
        });
        contentPanel.add(btnCancel);

        // OK Button
        RoundedCornerButton btnOK = new RoundedCornerButton("OK", new Color(220, 20, 60));
        btnOK.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        btnOK.setBounds(480, 480, 120, 50);
        
        btnOK.addActionListener(e -> {
            // Step 1: Validate password first
            if (!validatePassword()) return;

            // Step 2: Confirm deactivation
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to deactivate your account?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION
            );

            // Step 3: Deactivate if confirmed
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                    this,
                    "Your account has been deactivated.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose(); // Close current window
                new Login().setVisible(true); // Open login page
            }
        });

        contentPanel.add(btnOK);

        return contentPanel;
    }

    private JPanel createLabeledField(String labelText, RoundedPasswordField field, int y) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 35, 330, 45);

        field.setBounds(0, 0, 330, 45);
        field.setEchoChar('•');
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 50));
        layeredPane.add(field, JLayeredPane.DEFAULT_LAYER);

        JLabel eyeLabel = new JLabel();
        eyeLabel.setBounds(285, 10, 25, 25);
        eyeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        URL eyeURL = getClass().getResource("/Resources/eye.png");
        URL eyeClosedURL = getClass().getResource("/Resources/hide.png");
        if (eyeURL != null && eyeClosedURL != null) {
            ImageIcon openIcon = new ImageIcon(new ImageIcon(eyeURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            ImageIcon closedIcon = new ImageIcon(new ImageIcon(eyeClosedURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            eyeLabel.setIcon(closedIcon);
            eyeLabel.addMouseListener(new MouseAdapter() {
                boolean visible = false;
                @Override
                public void mouseClicked(MouseEvent e) {
                    visible = !visible;
                    field.setEchoChar(visible ? (char) 0 : '•');
                    eyeLabel.setIcon(visible ? openIcon : closedIcon);
                }
            });
        }
        layeredPane.add(eyeLabel, JLayeredPane.PALETTE_LAYER);
        panel.add(layeredPane);

        JLabel lblPassword = new JLabel(labelText);
        lblPassword.setBounds(0, 0, 150, 30);
        lblPassword.setFont(new Font("Tw Cen MT", Font.BOLD, 20)); // Label Font 
        lblPassword.setForeground(Color.WHITE);
        panel.add(lblPassword);

        panel.setBounds(285, y, 330, 90);
        return panel;
    }

    private void deactivateAccount() {
        if (!validatePassword()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to deactivate your account?", "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Your account has been deactivated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Login().setVisible(true);
        }
    }

    private RoundedButton createMenuButton(String text, String iconPath) {
        RoundedButton btn = new RoundedButton(text);
        URL iconURL = getClass().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        }
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 60));
        btn.setPreferredSize(new Dimension(250, 60));
        btn.setBackground(new Color(30,127,179));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(15);
        btn.setFocusPainted(false);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(20,100,150)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(30,127,179)); }
        });
        return btn;
    }

    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class RoundedCornerButton extends JButton {
        private final Color baseColor;
        private Color currentColor;
        public RoundedCornerButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.currentColor = color;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() { setBorder(BorderFactory.createEmptyBorder(5,15,5,15)); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),getHeight(),getHeight());
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,getHeight(),getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private boolean validatePassword() {
        String password = new String(pwtPassword.getPassword());
        
        if (password.isEmpty()) {
            errPassword.setText("Password is required!");
            return false;
        }
        
        if (!password.equals("Aung1234@@")) {
            errPassword.setText("Incorrect password!");
            return false;
        }
        
        errPassword.setText(""); // Correct password → clear error
        return true;
    }


    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeactivateAccount().setVisible(true));
    }
}
