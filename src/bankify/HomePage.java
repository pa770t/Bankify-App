package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class HomePage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;
    private JLabel balanceLabel;
    private boolean balanceVisible = true;
    private double balanceAmount = 100000.00;

    public HomePage() {
        setTitle("Bankify - Home Page");
        // Screen size updated to 1200x800
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Sidebar
        Sidebar sidebar = new Sidebar(this, "Home");

        // Content
        contentPanel = createContentPanel();

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(30,127,179));
        contentPanel.setLayout(null);

        // User Panel (Size adjusted for 1200 width)
        RoundedPanel userPanel = new RoundedPanel();
        userPanel.setBounds(70, 70, 220, 80);
        userPanel.setBackground(new Color(0, 191, 255));
        userPanel.setLayout(null);

        JLabel userIcon = new JLabel();
        userIcon.setBounds(20, 14, 55, 55);
        URL userIconURL = getClass().getResource("/Resources/my_profile.png");
        if (userIconURL != null) {
            Image img = new ImageIcon(userIconURL).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            userIcon.setIcon(new ImageIcon(img));
        }
        userPanel.add(userIcon);

        JLabel userLabel = new JLabel("Aung Aung");
        userLabel.setBounds(70, 22, 140, 35);
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Increased Font Size
        userPanel.add(userLabel);

        // Balance Panel
        RoundedPanel balancePanel = new RoundedPanel();
        balancePanel.setBounds(70, 200, 470, 80);
        balancePanel.setBackground(new Color(0, 191, 255));
        balancePanel.setLayout(null);

        JLabel balanceIcon = new JLabel();
        balanceIcon.setBounds(20, 20, 43, 43);
        URL balanceIconURL = getClass().getResource("/Resources/Balance.png");
        if (balanceIconURL != null) {
            Image img = new ImageIcon(balanceIconURL).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            balanceIcon.setIcon(new ImageIcon(img));
        }
        balancePanel.add(balanceIcon);

        balanceLabel = new JLabel("Account Balance: " + String.format("%.2f", balanceAmount) + " MMK");
        balanceLabel.setBounds(75, 22, 370, 35);
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); // Increased Font Size
        balancePanel.add(balanceLabel);

        JButton eyeButton = new JButton();
        eyeButton.setBounds(410, 22, 35, 35);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setBorderPainted(false);
        eyeButton.setFocusPainted(false);
        eyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        URL eyeOpenURL = getClass().getResource("/Resources/eye.png");
        URL eyeClosedURL = getClass().getResource("/Resources/hide.png");
        
        if (eyeOpenURL != null && eyeClosedURL != null) {
            ImageIcon eyeOpenIcon = new ImageIcon(new ImageIcon(eyeOpenURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            ImageIcon eyeClosedIcon = new ImageIcon(new ImageIcon(eyeClosedURL).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            eyeButton.setIcon(eyeOpenIcon);
            eyeButton.addActionListener(e -> {
                balanceVisible = !balanceVisible;
                balanceLabel.setText(balanceVisible ? "Account Balance: " + String.format("%.2f", balanceAmount) + " MMK" : "Account Balance: ****** MMK");
                eyeButton.setIcon(balanceVisible ? eyeOpenIcon : eyeClosedIcon);
            });
        }
        balancePanel.add(eyeButton);

        contentPanel.add(userPanel);
        contentPanel.add(balancePanel);
        
        // --- Buttons Section ---
        // Deposit
        JLabel depositIconLabel = new JLabel("");
        depositIconLabel.setBounds(160, 395, 85, 85);
        ImageIcon dIcon = new ImageIcon(HomePage.class.getResource("/Resources/deposit.png"));
        if (dIcon != null) depositIconLabel.setIcon(new ImageIcon(dIcon.getImage().getScaledInstance(85, 85, Image.SCALE_SMOOTH)));
        contentPanel.add(depositIconLabel);
        
        JButton depositButton = new RoundedButton2("Deposit", new Color(50, 205, 50));
        depositButton.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        depositButton.setBounds(135, 520, 150, 57);
        depositButton.addActionListener(e -> openDepositPage());
        contentPanel.add(depositButton);
        
        // Withdraw
        JLabel withdrawIconLabel = new JLabel("");
        withdrawIconLabel.setBounds(400, 395, 80, 80);
        ImageIcon wIcon = new ImageIcon(HomePage.class.getResource("/Resources/withdraw.png"));
        if (wIcon != null) withdrawIconLabel.setIcon(new ImageIcon(wIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        contentPanel.add(withdrawIconLabel);
        
        JButton withdrawButton = new RoundedButton2("Withdraw", new Color(220, 20, 60));
        withdrawButton.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        withdrawButton.setBounds(375, 520, 150, 57);
        withdrawButton.addActionListener(e -> openWithdrawPage());
        contentPanel.add(withdrawButton);
        
        // Transfer
        JLabel transferIconLabel = new JLabel("");
        transferIconLabel.setBounds(640, 395, 80, 80);
        ImageIcon tIcon = new ImageIcon(HomePage.class.getResource("/Resources/transfer.png"));
        if (tIcon != null) transferIconLabel.setIcon(new ImageIcon(tIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        contentPanel.add(transferIconLabel);
        
        JButton transferButton = new RoundedButton2("Transfer", new Color(0, 191, 255));
        transferButton.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        transferButton.setBounds(615, 520, 150, 57);
        transferButton.addActionListener(e -> openTransferPage());
        contentPanel.add(transferButton);

        return contentPanel;
    }

    private void openDepositPage() {
        DepositPage depositPage = new DepositPage();
        depositPage.setSize(1200, 800);
        depositPage.setLocationRelativeTo(null);
        depositPage.setVisible(true);
        this.dispose();
    }

    private void openWithdrawPage() {
        WithdrawPage withdrawPage = new WithdrawPage();
        withdrawPage.setSize(1200, 800);
        withdrawPage.setLocationRelativeTo(null);
        withdrawPage.setVisible(true);
        this.dispose();
    }

    private void openTransferPage() {
        TransferPage transferPage = new TransferPage();
        transferPage.setSize(1200, 800);
        transferPage.setLocationRelativeTo(null);
        transferPage.setVisible(true);
        this.dispose();
    }

    // --- Inner Classes (Rounded UI) ---
    private class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 80, 80);
            g2.dispose();
        }
    }

    private class RoundedButton2 extends JButton {
        private Color currentColor;
        public RoundedButton2(String text, Color baseColor) {
            super(text);
            this.currentColor = baseColor;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE);
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomePage frame = new HomePage();
            frame.setVisible(true);
        });
    }
}