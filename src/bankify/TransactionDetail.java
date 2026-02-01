package bankify;

import bankify.dao.CustomerDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class TransactionDetail extends JPanel {
    private Customer customer;
    private CustomerDao customerDao;

    // ===== FIXED constructor: added customer and customerDao parameters =====
    public TransactionDetail(Transaction tx, CardLayout cardLayout, JPanel contentPanel, Customer customer, CustomerDao customerDao) {
        this.customer = customer;
        this.customerDao = customerDao;

        setLayout(new BorderLayout());
        setBackground(new Color(235, 238, 242));

        // Get the top-level window (JFrame) to pass to the sidebar
        SwingUtilities.invokeLater(() -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            Sidebar sidebar = new Sidebar(parentFrame, "Transactions", customer, customerDao);
            add(sidebar, BorderLayout.WEST);
            revalidate();
            repaint();
        });

        // ===== Create Main Content =====
        JPanel mainContent = createMainContent(tx, cardLayout, contentPanel);
        add(mainContent, BorderLayout.CENTER);
    }

    // ===== Main Content Creation Method =====
    private JPanel createMainContent(Transaction tx, CardLayout cardLayout, JPanel contentPanel) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(235, 238, 242));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // ===== Title =====
        JLabel titleLabel = new JLabel("Transaction Detail");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(25));

        // ===== Transaction Card =====
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        cardPanel.setMaximumSize(new Dimension(800, 500));
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Transaction Type with icon
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        typePanel.setBackground(Color.WHITE);

        String iconName = "";
        switch (tx.getTransactionType().toLowerCase()) {
            case "deposit": iconName = "/Resources/deposit.png"; break;
            case "withdraw": iconName = "/Resources/withdraw.png"; break;
            case "transfer": case "send": case "receive": iconName = "/Resources/transfer.png"; break;
            default: iconName = "/Resources/transactions.png";
        }

        JLabel typeIcon = new JLabel();
        URL typeIconURL = getClass().getResource(iconName);
        if (typeIconURL != null) {
            ImageIcon icon = new ImageIcon(typeIconURL);
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            typeIcon.setIcon(new ImageIcon(img));
        }
        typePanel.add(typeIcon);

        JLabel typeLabel = new JLabel(tx.getTransactionType());
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        typeLabel.setForeground(getTypeColor(tx.getTransactionType()));
        typePanel.add(typeLabel);

        cardPanel.add(typePanel, BorderLayout.NORTH);

        // ===== Details Panel =====
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Amount row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        detailsPanel.add(createDetailLabel("Amount:", true, 20), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        JLabel amountLabel = createDetailLabel(String.format("%,.2f", tx.getAmount()) + " MMK", false, 28);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        if (tx.getTransactionType().equalsIgnoreCase("Deposit") || tx.getTransactionType().equalsIgnoreCase("Receive")) {
            amountLabel.setForeground(new Color(0, 150, 0));
        } else {
            amountLabel.setForeground(new Color(200, 0, 0));
        }
        detailsPanel.add(amountLabel, gbc);

        // Transaction ID row
        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(createDetailLabel("Transaction ID:", true, 18), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createDetailLabel(String.valueOf(tx.getFormattedDate()), false, 18), gbc);

        // Date row
        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createDetailLabel("Date & Time:", true, 18), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createDetailLabel(String.valueOf(tx.getFormattedDate()), false, 18), gbc);

        // Description row
        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(createDetailLabel("Description:", true, 18), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createDetailLabel(tx.getDescription(), false, 18), gbc);

        // Status row
        gbc.gridx = 0; gbc.gridy = 4;
        detailsPanel.add(createDetailLabel("Status:", true, 18), gbc);
        gbc.gridx = 1;
        JLabel statusLabel = createDetailLabel(tx.getStatus(), false, 20);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statusLabel.setForeground(tx.getStatus().equalsIgnoreCase("Success") ? new Color(0, 150, 0) : new Color(200, 0, 0));
        detailsPanel.add(statusLabel, gbc);

        cardPanel.add(detailsPanel, BorderLayout.CENTER);

        mainPanel.add(cardPanel);
        mainPanel.add(Box.createVerticalStrut(40));

        // ===== Action Buttons =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(new Color(235, 238, 242));
        buttonPanel.add(Box.createHorizontalGlue());

        RoundedButton backButton = new RoundedButton("← Back to Transactions");
        backButton.setPreferredSize(new Dimension(280, 55));
        backButton.setMinimumSize(new Dimension(280, 55));
        backButton.setMaximumSize(new Dimension(280, 55));
        backButton.setBackground(new Color(30, 127, 179));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { backButton.setBackground(new Color(20, 100, 150)); }
            @Override
            public void mouseExited(MouseEvent e) { backButton.setBackground(new Color(30, 127, 179)); }
        });

        backButton.addActionListener(e -> cardLayout.show(contentPanel, "Transactions"));
        buttonPanel.add(backButton);

        buttonPanel.add(Box.createHorizontalGlue());

        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private Color getTypeColor(String type) {
        switch (type.toLowerCase()) {
            case "deposit": case "receive": return new Color(0, 150, 0);
            case "withdraw": case "send": case "transfer": return new Color(200, 0, 0);
            default: return Color.BLACK;
        }
    }

    private JLabel createDetailLabel(String text, boolean isBold, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(isBold ? new Color(70, 70, 70) : Color.BLACK);
        return label;
    }

    private JLabel createDetailLabel(String text, boolean isBold) {
        return createDetailLabel(text, isBold, 14);
    }

    private static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Transaction Detail Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);

            CardLayout cardLayout = new CardLayout();
            JPanel contentPanel = new JPanel(cardLayout);

            Transaction tx = new Transaction();
            Customer customer = new Customer();       // pass real customer
            CustomerDao customerDao = new CustomerDao(DBConnection.getConnection());

            contentPanel.add(new TransactionDetail(tx, cardLayout, contentPanel, customer, customerDao), "Detail");

            frame.add(contentPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
