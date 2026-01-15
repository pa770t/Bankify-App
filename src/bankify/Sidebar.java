package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class Sidebar extends JPanel {
    private JFrame parentFrame;
    private String activePage; // Track which page is active

    public Sidebar(JFrame parentFrame, String activePage) {
        this.parentFrame = parentFrame;
        this.activePage = activePage;

        setPreferredSize(new Dimension(280, 0));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.WHITE);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        URL logoURL = getClass().getResource("/Resources/bank_logo.jpg");
        if (logoURL != null) {
            ImageIcon logoIcon = new ImageIcon(logoURL);
            Image img = logoIcon.getImage().getScaledInstance(220, 150, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        }
        header.add(logoLabel);
        header.add(Box.createVerticalStrut(10));

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Sidebar buttons - colors are handled inside createMenuButton based on activePage
        menuPanel.add(createMenuButton("Home", "/Resources/home.png"));
        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuButton("Deposit", "/Resources/deposit.png"));
        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuButton("Withdraw", "/Resources/withdraw.png"));
        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuButton("Transfer", "/Resources/transfer.png"));
        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuButton("Transactions", "/Resources/transactions.png"));
        menuPanel.add(Box.createVerticalStrut(15));

        menuPanel.add(createMenuButton("Settings", "/Resources/settings.png"));

        menuPanel.add(Box.createVerticalGlue());

        add(header, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
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
        btn.setMaximumSize(new Dimension(240, 60));
        btn.setPreferredSize(new Dimension(240, 60));

        // --- ACTIVE COLOR LOGIC ---
        if (text.equals(activePage)) {
            btn.setBackground(new Color(0, 191, 255)); // Active Color
        } else {
            btn.setBackground(new Color(30, 127, 179)); // Default Color
        }

        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(15);
        btn.setFocusPainted(false);

        // Action Listeners for navigation
        btn.addActionListener(e -> {
            if (text.equals("Home")) navigate(new HomePage());
            else if (text.equals("Deposit")) navigate(new DepositPage());
            else if (text.equals("Withdraw")) navigate(new WithdrawPage());
            else if (text.equals("Transfer")) navigate(new TransferPage());
            else if (text.equals("Settings")) navigate(new MainSettings());
            else if (text.equals("Transactions")) openTransactionsPage();
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(activePage)) btn.setBackground(new Color(20,100,150));
            }
            public void mouseExited(MouseEvent e) {
                if (!text.equals(activePage)) btn.setBackground(new Color(30,127,179));
                else btn.setBackground(new Color(0, 191, 255));
            }
        });

        return btn;
    }

    private void navigate(JFrame page) {
        page.setSize(1200, 800);
        page.setLocationRelativeTo(null);
        page.setVisible(true);
        parentFrame.dispose();
    }

    private void openTransactionsPage() {
        JFrame transactionsFrame = new JFrame("Bankify - Transactions");
        transactionsFrame.setSize(1200, 800);
        CardLayout cardLayout = new CardLayout();
        JPanel content = new JPanel(cardLayout);
        TransactionsPage tp = new TransactionsPage(cardLayout, content, transactionsFrame);
        content.add(tp, "Transactions");
        transactionsFrame.add(content);
        transactionsFrame.setLocationRelativeTo(parentFrame);
        transactionsFrame.setVisible(true);
        parentFrame.setVisible(false);
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
            g2.fillRoundRect(0,0,getWidth(),getHeight(),60,60);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}