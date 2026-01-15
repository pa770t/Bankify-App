package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class ChangePassword extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;

    // Password fields
    private RoundedPasswordField pwtCurrent, pwtNew, pwtConfirm;

    public ChangePassword() {
        setTitle("Bankify - Change Password");
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

        // Header Panel - အချိုးအစား ချိန်ညှိခြင်း
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
        settingsHeaderPanel.setBounds(330, 60, 260, 70);

        JLabel settingsIconLabel = new JLabel("");
        settingsIconLabel.setBounds(20, 15, 40, 40);
        URL settingsIconURL = getClass().getResource("/Resources/change_password.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            settingsIconLabel.setIcon(new ImageIcon(img));
        }
        settingsHeaderPanel.add(settingsIconLabel);

        JLabel settingsLabel = new JLabel("Change Password");
        settingsLabel.setBounds(65, 15, 180, 40);
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        // Header Font Size ကို 22 သို့ တိုးထားပါတယ်
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22));
        settingsHeaderPanel.add(settingsLabel);

        contentPanel.add(settingsHeaderPanel);

        // Back Button
        JButton btnBack = new JButton();
        btnBack.setBounds(30, 60, 70, 50);
        btnBack.setBackground(new Color(30, 127, 179));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setIcon(new ImageIcon(ChangePassword.class.getResource("/Resources/exit_arrow.png")));
        btnBack.addActionListener(e -> {
            dispose();
            new MainSettings().setVisible(true);
        });
        contentPanel.add(btnBack);

        // Password Fields Positioning (1200x800 အတွက် Center ကျအောင် ချိန်ထားပါတယ်)
        pwtCurrent = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Current Password", pwtCurrent, 200));

        pwtNew = new RoundedPasswordField();
        contentPanel.add(createLabeledField("New Password", pwtNew, 310));

        pwtConfirm = new RoundedPasswordField();
        contentPanel.add(createLabeledField("Confirm Password", pwtConfirm, 420));

        // OK Button
        RoundedCornerButton btnOK = new RoundedCornerButton("OK");
        btnOK.setBounds(400, 550, 130, 50);
        btnOK.setFont(new Font("Tw Cen MT", Font.BOLD, 18)); // Button font 18
        btnOK.addActionListener(e -> {
            if (validatePassword()) {
                JOptionPane.showMessageDialog(this,
                        "Password changed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainSettings().setVisible(true);
            }
        });

        contentPanel.add(btnOK);

        return contentPanel;
    }

    private JPanel createLabeledField(String labelText, RoundedPasswordField field, int y) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Label Font 18 သို့ တိုးထားပါတယ်
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        label.setBounds(0, 0, 300, 25);
        panel.add(label);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 35, 330, 45); // Field ကို ပိုရှည်ပြီး ပိုမြင့်အောင်လုပ်ထားပါတယ်

        field.setBounds(0, 0, 330, 45);
        field.setEchoChar('•');
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Input text font 18
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

        panel.setBounds(300, y, 330, 90); // Panel position ချိန်ညှိမှု
        return panel;
    }

    private class RoundedCornerButton extends JButton {
        private static final long serialVersionUID = 1L;
        private Color currentColor;
        public RoundedCornerButton(String text) {
            super(text);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            currentColor = new Color(220,20,60);
            setForeground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = currentColor.darker(); repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = new Color(220,20,60); repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0,0,getWidth(),getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class RoundedPasswordField extends JPasswordField {
        private static final long serialVersionUID = 1L;
        public RoundedPasswordField() {
            setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        }
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
        String current = new String(pwtCurrent.getPassword());
        String newPass = new String(pwtNew.getPassword());
        String confirm = new String(pwtConfirm.getPassword());

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String strongPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";
        if (!newPass.matches(strongPattern)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters with upper, lower, number and special char.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Mismatch", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String savedPassword = "Aung1234@@"; // dummy
        if (!current.equals(savedPassword)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChangePassword().setVisible(true));
    }
}