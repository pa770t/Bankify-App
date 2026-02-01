package bankify;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bankify.dao.CustomerDao;
import bankify.dao.TransactionDao;
import bankify.service.PageGuardService;

public class TransactionsPage extends JPanel {

    private static Customer customer;
    private static CustomerDao customerDao;

    // NOTE: Removed 'transactionDao' as a field. We only create it when needed.

    public TransactionsPage(CardLayout cardLayout, JPanel contentPanel, JFrame parentFrame, Customer customer,
                            CustomerDao customerDao) {
        // 1. Session Guard
        if (customer == null) {
            if (parentFrame != null) parentFrame.dispose();
            PageGuardService.checkSession(this, customer);
            return;
        }

        // 2. Setup Global State
        TransactionsPage.customer = customer;
        TransactionsPage.customerDao = customerDao;

        // 3. UI Setup
        setLayout(new BorderLayout());
        setBackground(new Color(30, 127, 179));

        // 4. Sidebar
        Sidebar sidebar = new Sidebar(parentFrame, "Transactions", customer, customerDao);
        add(sidebar, BorderLayout.WEST);

        // 5. Main Content
        JPanel mainContent = createMainContent(cardLayout, contentPanel);
        add(mainContent, BorderLayout.CENTER);

        // 6. FORCE EXIT FIX: Ensure the parent frame kills the app on close
        forceExitOnClose(parentFrame);
    }

    /**
     * This method guarantees that clicking 'X' kills the JVM.
     * It removes old listeners to avoid duplicates and adds a fresh "System.exit(0)".
     */
    private void forceExitOnClose(JFrame frame) {
        if (frame == null) return;

        // Reset default operation to DO_NOTHING so our listener handles it fully
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Remove existing window listeners to prevent conflicts
        for (java.awt.event.WindowListener wl : frame.getWindowListeners()) {
            frame.removeWindowListener(wl);
        }

        // Add the "Nuclear" close listener
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Application closing via TransactionsPage...");
                frame.dispose();
                System.exit(0); // Stops all "Ghost" threads and DB connections
            }
        });
    }

    private JPanel createMainContent(CardLayout cardLayout, JPanel contentPanel) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(30, 127, 179));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 30, 40));

        // --- Title Section ---
        JLabel titleLabel = new JLabel("Transactions");
        ImageIcon titleIcon = loadIcon("/Resources/transactions.png", 40, 40);
        if (titleIcon != null) titleLabel.setIcon(titleIcon);
        titleLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIconTextGap(20);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        titleLabel.setUI(new PillLabelUI(new Color(0, 191, 255), 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(35));

        // --- REAL DATA LOGIC (FIXED CONNECTION LEAK) ---
        List<Transaction> dbTransactions = new ArrayList<>();

        // Try-with-resources: Automatically closes Connection when done.
        try (Connection conn = DBConnection.getConnection()) {
            TransactionDao dao = new TransactionDao(conn);
            dbTransactions = dao.getAllTransactionByCustomer(customer.getCustomerId());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
        // --- END DATA LOGIC ---

        // --- List Container ---
        JPanel transactionContainer = new JPanel();
        transactionContainer.setLayout(new BoxLayout(transactionContainer, BoxLayout.Y_AXIS));
        transactionContainer.setBackground(new Color(30, 127, 179));

        for (Transaction tx : dbTransactions) {
            transactionContainer.add(createTransactionItem(tx, cardLayout, contentPanel));
            transactionContainer.add(Box.createVerticalStrut(15));
        }

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(transactionContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(30, 127, 179));

        mainPanel.add(scrollPane);
        return mainPanel;
    }

    private JPanel createTransactionItem(Transaction tx, CardLayout cl, JPanel mainContainer) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(173, 216, 230));
        panel.setMaximumSize(new Dimension(1000, 110));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(135, 206, 250), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 1. Icon
        JLabel userImageLabel = new JLabel();
        userImageLabel.setPreferredSize(new Dimension(70, 70));
        ImageIcon userIcon = loadIcon("/Resources/user.png", 60, 60);
        if (userIcon != null) userImageLabel.setIcon(new ImageIcon(makeCircular(userIcon.getImage())));
        else userImageLabel.setIcon(createColoredCircleIcon(tx.getTransactionType()));
        panel.add(userImageLabel, BorderLayout.WEST);

        // 2. Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Row 1: Type, ID, Amount
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row1.setOpaque(false);

        JLabel typeLabel = new JLabel(tx.getTransactionType());
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        typeLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        boolean isIncome = tx.getTransactionType().equalsIgnoreCase("Deposit") ||
                tx.getTransactionType().equalsIgnoreCase("Receive");
        typeLabel.setUI(new PillLabelUI(isIncome ? new Color(60, 179, 113) : new Color(220, 20, 60), 30));
        row1.add(typeLabel);

        JLabel idLabel = new JLabel("ID:" + tx.getTransactionId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        idLabel.setForeground(new Color(50, 50, 50));
        row1.add(idLabel);

        // FORMATTED AMOUNT FIX
        JLabel amountLabel = new JLabel();
        String formattedAmount = String.format("%,.2f", tx.getAmount());

        if (isIncome) {
            amountLabel.setForeground(new Color(40, 140, 80));
            amountLabel.setText("+" + formattedAmount + " MMK");
        } else {
            amountLabel.setForeground(new Color(190, 20, 50));
            amountLabel.setText("-" + formattedAmount + " MMK");
        }
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        row1.add(amountLabel);

        // Row 2: Date, Status
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setOpaque(false);

        JLabel dateLabel = new JLabel(tx.getFormattedDate()); // Assuming Transaction.java has this method
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dateLabel.setForeground(new Color(80, 80, 80));
        row2.add(dateLabel);

        JLabel statusLabel = new JLabel(tx.getStatus());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 15, 4, 15));
        statusLabel.setUI(new PillLabelUI(tx.getStatus().equalsIgnoreCase("Success") ?
                new Color(60, 179, 113) : new Color(220, 20, 60), 25));
        row2.add(statusLabel);

        infoPanel.add(row1);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(row2);
        panel.add(infoPanel, BorderLayout.CENTER);

        // Click Logic
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    TransactionDetail detail = new TransactionDetail(tx, cl, mainContainer, customer, customerDao);
                    mainContainer.add(detail, "Detail");
                    cl.show(mainContainer, "Detail");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error opening detail: " + ex.getMessage());
                }
            }
            public void mouseEntered(MouseEvent e) { panel.setBackground(new Color(135, 206, 250)); }
            public void mouseExited(MouseEvent e) { panel.setBackground(new Color(173, 216, 230)); }
        });

        return panel;
    }

    // --- Helpers (Icons/UI) ---
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) { /* ignore */ }
        return null;
    }

    private Image makeCircular(Image srcImg) {
        int size = 60;
        BufferedImage mask = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mask.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillOval(0, 0, size, size);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawImage(srcImg, 0, 0, size, size, null);
        g2.dispose();
        return mask;
    }

    private Icon createColoredCircleIcon(String type) {
        int size = 60;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(new Color(100, 149, 237));
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(image);
    }

    private static class PillLabelUI extends BasicLabelUI {
        private Color bg;
        private int height;
        public PillLabelUI(Color bg, int height) { this.bg = bg; this.height = height; }
        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
            super.paint(g2, c);
            g2.dispose();
        }
    }

    // --- LAUNCHER LOGIC ---

    public static void launch(CardLayout cardLayout, JPanel contentPanel, JFrame parentFrame, Customer customer,
                              CustomerDao customerDao) {
        if (customer == null) {
            if (parentFrame != null) parentFrame.dispose();
            new Login().setVisible(true);
        } else {
            // 1. Clear old panels to prevent "Empty Window" stacking
            contentPanel.removeAll();

            // 2. Create new Page
            TransactionsPage page = new TransactionsPage(cardLayout, contentPanel, parentFrame, customer, customerDao);
            contentPanel.add(page, "Transactions");

            // 3. FORCE SHOW the card (Fixes "Empty Window" bug)
            cardLayout.show(contentPanel, "Transactions");

            // 4. Refresh Frame
            parentFrame.setContentPane(contentPanel);
            parentFrame.revalidate();
            parentFrame.repaint();
            parentFrame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (customer == null) {
                // If no user, kill this main and open Login instead
                Login login = new Login();
                login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                login.setVisible(true);
                return;
            }

            JFrame frame = new JFrame("Bankify - Transactions");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);

            CardLayout cl = new CardLayout();
            JPanel cp = new JPanel(cl);

            TransactionsPage.launch(cl, cp, frame, customer, customerDao);
        });
    }
}