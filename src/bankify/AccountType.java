package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class AccountType extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;

    public AccountType() {
        setTitle("Bankify - Account Type");
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
        settingsHeaderPanel.setBounds(315, 60, 250, 70);

        JLabel settingsIconLabel = new JLabel("");
        settingsIconLabel.setBounds(20, 12, 45, 45);
        URL settingsIconURL = getClass().getResource("/Resources/account_type.png");
        if (settingsIconURL != null) {
            ImageIcon icon = new ImageIcon(settingsIconURL);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            settingsIconLabel.setIcon(new ImageIcon(img));
        }
        settingsHeaderPanel.add(settingsIconLabel);

        JLabel settingsLabel = new JLabel("Account Type");
        settingsLabel.setBounds(65, 12, 170, 45);
        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        settingsLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        settingsHeaderPanel.add(settingsLabel);

        contentPanel.add(settingsHeaderPanel);
        
        Font listFont = new Font("Tw Cen MT", Font.BOLD, 20);

        // User Option
        RoundedPanel userPanel = new RoundedPanel();
        userPanel.setBounds(260, 250, 350, 60);
        userPanel.setBackground(new Color(0, 191, 255));
        userPanel.setLayout(null);

        JLabel userIcon = new JLabel();
        userIcon.setBounds(30, 10, 40, 40);
        URL userIconURL = getClass().getResource("/Resources/user.png");
        if (userIconURL != null) {
            Image img = new ImageIcon(userIconURL).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            userIcon.setIcon(new ImageIcon(img));
        }
        userPanel.add(userIcon);

        JLabel userLabel = new JLabel("User");
        userLabel.setBounds(85, 12, 200, 35);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(listFont);
        userPanel.add(userLabel);

        StaticRadioButton userRadio = new StaticRadioButton(true);
        userRadio.setBounds(220, 260, 40, 40);
        userRadio.setSelected(true);

        // Agent Option
        RoundedPanel agentPanel = new RoundedPanel();
        agentPanel.setBounds(260, 340, 350, 60);
        agentPanel.setBackground(new Color(0, 191, 255));
        agentPanel.setLayout(null);

        JLabel agentIcon = new JLabel();
        agentIcon.setBounds(30, 10, 40, 40);
        URL agentIconURL = getClass().getResource("/Resources/agent.png");
        if (agentIconURL != null) {
            Image img = new ImageIcon(agentIconURL).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            agentIcon.setIcon(new ImageIcon(img));
        }
        agentPanel.add(agentIcon);

        JLabel agentLabel = new JLabel("Agent");
        agentLabel.setBounds(85, 12, 200, 35);
        agentLabel.setForeground(Color.WHITE);
        agentLabel.setFont(listFont);
        agentPanel.add(agentLabel);

        StaticRadioButton agentRadio = new StaticRadioButton(false);
        agentRadio.setBounds(220, 350, 40, 40);
        agentRadio.setSelected(false);

        contentPanel.add(userPanel);
        contentPanel.add(agentPanel);
        contentPanel.add(userRadio);
        contentPanel.add(agentRadio);

        // Back Button
        JButton btnBack = new JButton();
        btnBack.setBounds(30, 65, 75, 60);
        btnBack.setBackground(new Color(30, 127, 179));
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        URL exitArrowURL = getClass().getResource("/Resources/exit_arrow.png");
        if (exitArrowURL != null) {
            btnBack.setIcon(new ImageIcon(exitArrowURL));
        }
        btnBack.addActionListener(e -> {
            dispose();
            new MainSettings().setVisible(true);
        });
        contentPanel.add(btnBack);

        // OK Button - Updated Action Listener
        RoundedCornerButton btnOK = new RoundedCornerButton("OK", new Color(220, 20, 60));
        btnOK.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        btnOK.setBounds(375, 460, 120, 50);
        btnOK.addActionListener(e -> {
            dispose(); // လက်ရှိ frame ကို ပိတ်မယ်
            new MainSettings().setVisible(true); // settings_main page ကို ဖွင့်မယ်
        });
        contentPanel.add(btnOK);

        return contentPanel;
    }

    private class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Color bgColor = new Color(0, 191, 255);
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
        }
    }

    private class RoundedCornerButton extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color baseColor;
        private Color currentColor;
        public RoundedCornerButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            this.currentColor = color;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { currentColor = baseColor.darker(); repaint(); }
                public void mouseExited(MouseEvent e) { currentColor = baseColor; repaint(); }
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
            super.paintComponent(g);
        }
    }

    private class StaticRadioButton extends JToggleButton {
        private static final long serialVersionUID = 1L;
        private ImageIcon onIcon, offIcon;
        public StaticRadioButton(boolean initiallySelected) {
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setOpaque(false);
            URL onURL = getClass().getResource("/Resources/radio_on.png");
            URL offURL = getClass().getResource("/Resources/radio_off.png");
            if(onURL != null && offURL != null) {
                onIcon = new ImageIcon(new ImageIcon(onURL).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
                offIcon = new ImageIcon(new ImageIcon(offURL).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            }
            setSelected(initiallySelected);
        }
        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            if (onIcon != null && offIcon != null) {
                setIcon(selected ? onIcon : offIcon);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountType frame = new AccountType();
            frame.setVisible(true);
        });
    }

}
