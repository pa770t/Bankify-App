package bankify.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class AdminSettingsPage extends JFrame {

    // Main Color Definition
    private final Color PRIMARY_COLOR = new Color(30, 127, 179);
    private final Color PILL_COLOR = new Color(0, 191, 255); 

    public AdminSettingsPage() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Bankify Admin - Settings");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Add Sidebar
        AdminSidebar sidebar = new AdminSidebar(this, "Settings");
        add(sidebar, BorderLayout.WEST);

        // 2. Add Main Content
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(PRIMARY_COLOR);
        contentPanel.setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();

     // --- 1. Header (Pill Shape with Icon) ---
        // အခြားပေ့ခ်ျတွေနဲ့ အမြင့် (y-axis) တူအောင် Insets ရဲ့ top ကို 60 ပေးပါမယ်
        // GridBagLayout မှာ အခြားပေ့ခ်ျတွေလို အတိအကျနေရာချနိုင်ဖို့ Insets ကို အခုလိုပြင်ပါ-
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        
        // အပေါ်ကနေ 60px ချလိုက်တာဟာ အခြားပေ့ခ်ျတွေရဲ့ setBounds(x, 60, w, h) နဲ့ တူသွားစေပါတယ်
        gbc.insets = new Insets(60, 0, 80, 0); 
        gbc.anchor = GridBagConstraints.CENTER; // PAGE_START အစား CENTER သုံးခြင်းဖြင့် ပိုညီသွားပါမယ်
        
        PillPanel headerPill = new PillPanel("Settings", "/Resources/settings2.png");
        // Pill size ကိုလည်း အခြားပေ့ခ်ျတွေနဲ့ တူအောင် (260, 70) လို့ အောက်က PillPanel class မှာ ပြင်ပါမယ်
        contentPanel.add(headerPill, gbc);

        // --- 2. Menu Options Grid ---
        // အကွာအဝေး သတ်မှတ်ချက်များ (ဒီဂဏန်းတွေကို လိုသလို ထပ်ပြင်နိုင်ပါတယ်)
        int hGap = 80; // ဘယ်/ညာ ခလုတ်နှစ်ခုကြား အကွာအဝေး
        int vGap = 80; // အပေါ်တန်းနဲ့ အောက်တန်းကြား အကွာအဝေး
        
        // -- Row 1, Col 1: My Profile --
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        // Right ဘက်မှာ hGap တစ်ဝက်, Bottom မှာ vGap ထည့်ပါတယ်
        gbc.insets = new Insets(0, 0, vGap, hGap/2); 
        gbc.anchor = GridBagConstraints.CENTER;
        addMenuOption(contentPanel, gbc, "<html><center>Admin<br>Profile</center></html>", "/Resources/my_profile.png", e -> openMyProfilePage());

        // -- Row 1, Col 2: Change Password --
        gbc.gridx = 1;
        gbc.gridy = 1;
        // Left ဘက်မှာ hGap တစ်ဝက်, Bottom မှာ vGap
        gbc.insets = new Insets(0, hGap/2, vGap, 0);
        addMenuOption(contentPanel, gbc, "<html><center>Change<br>Password</center></html>", "/Resources/change_password.png", e -> openChangePasswordPage());

        // -- Row 2, Col 1: Deactivate Account --
        gbc.gridx = 0;
        gbc.gridy = 2;
        // Row 2 ဖြစ်တဲ့အတွက် Bottom မှာ space မလိုတော့ပါ (0 ထားလိုက်ပါတယ်)
        gbc.insets = new Insets(0, 0, 0, hGap/2);
        addMenuOption(contentPanel, gbc, "<html><center>Deactivate<br>Account</center></html>", "/Resources/deactivate_account.png", e -> openDeactivateAccountPage());

        // -- Row 2, Col 2: Logout --
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, hGap/2, 0, 0);
        addMenuOption(contentPanel, gbc, "Log Out", "/Resources/logout.png", e -> openLogoutPage());

        // --- 3. Pusher Component ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0); // Reset insets
        gbc.weighty = 1.0; 
        contentPanel.add(Box.createGlue(), gbc);

        return contentPanel;
    }

    // Helper to add Icon + Button pair
    private void addMenuOption(JPanel panel, GridBagConstraints gbc, String text, String iconPath, java.awt.event.ActionListener action) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setOpaque(false); 

        // 1. Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        URL imgUrl = getClass().getResource(iconPath);
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
        } else {
            iconLabel.setText("Icon Missing");
            iconLabel.setForeground(Color.WHITE);
        }
        
        // 2. Button
        RoundedButton2 btn = new RoundedButton2(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(160, 60));
        btn.setMaximumSize(new Dimension(160, 60)); 
        btn.addActionListener(action);

        // Add with spacing
        itemPanel.add(iconLabel);
        itemPanel.add(Box.createVerticalStrut(15)); 
        itemPanel.add(btn);

        panel.add(itemPanel, gbc);
    }

// --- Navigation Methods ---
    
    // 1. My Profile Page သို့သွားရန်
    private void openMyProfilePage() {
        // လက်ရှိ Settings Page ကို ပိတ်မည်
        this.dispose();
        // AdminMyProfile Page အသစ်ကို ဖွင့်မည်
        new bankify.admin.AdminMyProfile().setVisible(true);
    }

    // 2. Change Password Page သို့သွားရန်
    private void openChangePasswordPage() {
        this.dispose();
        // Change Password class နာမည်ကို မိမိပေးထားသည့်အတိုင်း ပြင်ဆင်ပါ
        new bankify.admin.AdminChangePassword().setVisible(true);
        
    }

    // 3. Deactivate Account Page သို့သွားရန်
    private void openDeactivateAccountPage() {
        this.dispose();
        // Deactivate Account class နာမည်ကို မိမိပေးထားသည့်အတိုင်း ပြင်ဆင်ပါ
        new bankify.admin.AdminDeactivateAccount().setVisible(true);
        
    }

    // 4. Logout ပြုလုပ်ရန်
    private void openLogoutPage() { 
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to log out?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            // Login Page သို့ ပြန်ပို့ရန် (Login class ရှိရာ path ကို ထည့်ပါ)
            new bankify.Login().setVisible(true);
            System.out.println("Logged out successfully.");
        }
    }
    // --- Custom Pill Panel with Icon ---
    private class PillPanel extends JPanel {
        private String text;
        private Image iconImage;

        public PillPanel(String text, String iconPath) {
            this.text = text;
            setOpaque(false);
            setPreferredSize(new Dimension(280, 70)); // Size slightly larger for icon
            
            // Load Icon
            URL url = getClass().getResource(iconPath);
            if (url != null) {
                this.iconImage = new ImageIcon(url).getImage();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Background Pill
            g2.setColor(PILL_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);

            // Draw Content (Icon + Text) Centered
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Tw Cen MT", Font.BOLD, 26));
            FontMetrics fm = g2.getFontMetrics();
            
            int iconSize = 40; // Icon width/height
            int gap = 15;      // Space between icon and text
            int textWidth = fm.stringWidth(text);
            
            // Calculate total width to center both
            int totalContentWidth = iconSize + gap + textWidth;
            int startX = (getWidth() - totalContentWidth) / 2;
            int centerY = getHeight() / 2;

            // Draw Icon
            if (iconImage != null) {
                g2.drawImage(iconImage, startX, centerY - (iconSize/2), iconSize, iconSize, this);
            }

            // Draw Text
            int textY = centerY + (fm.getAscent() / 2) - 2; // Adjust for baseline
            g2.drawString(text, startX + iconSize + gap, textY);
        }
    }

    // --- Rounded Button Class ---
    private class RoundedButton2 extends JButton {
        private static final long serialVersionUID = 1L;
        private Color hoverColor = new Color(30, 150, 255);
        private Color pressedColor = new Color(20, 120, 200);
        private Color currentColor = new Color(0, 191, 255);

        public RoundedButton2(String text) {
            super(text);
            setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
            setFont(new Font("Tw Cen MT", Font.BOLD, 18));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = hoverColor; repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = new Color(0, 191, 255); repaint(); }
                public void mousePressed(MouseEvent e) { currentColor = pressedColor; repaint(); }
                public void mouseReleased(MouseEvent e) { currentColor = hoverColor; repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminSettingsPage().setVisible(true));
    }
}