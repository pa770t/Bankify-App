package bankify;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import bankify.service.TransactionService;

public class TransactionsPage extends JPanel {

    // Transaction Data Model
    public static class Tx {
        String type;
        String agentId;
        String transactionId;
        String amount;
        String date;
        String status;
        String userImage; 

        public Tx(String type, String transactionId, String amount, String date, String status, String userImage) {
            this.type = type;
            this.transactionId = transactionId;
            this.amount = amount;
            this.date = date;
            this.status = status;
            this.userImage = userImage;
        }
    }

    private ArrayList<Tx> transactions;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public TransactionsPage(CardLayout cardLayout, JPanel contentPanel, JFrame parentFrame) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 127, 179));

        // Pass parentFrame (the JFrame) and the active page name "Transactions"
        Sidebar sidebar = new Sidebar(parentFrame, "Transactions");
        add(sidebar, BorderLayout.WEST);

        // Create Main Content
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(30, 127, 179));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 30, 40));

        JLabel titleLabel = new JLabel("Transactions");
        ImageIcon titleIcon = loadIcon("/Resources/transactions.png", 40, 40);
        if (titleIcon != null) titleLabel.setIcon(titleIcon);
        titleLabel.setFont(new Font("Tw Cen MT", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        titleLabel.setIconTextGap(20);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        titleLabel.setUI(new PillLabelUI(new Color(0, 191, 255), 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(35));

        // --- REAL DATA LOGIC START ---
        transactions = new ArrayList<>();
        TransactionService service = new TransactionService();

        // Replace with currently user id
        long currentUserId = 2;
        List<Map<String, Object>> dbData = service.getTransactionsForUser(currentUserId);

        if (dbData != null) {
            for (Map<String, Object> row : dbData) {
                // Mapping DB columns to your Tx model
                String type = row.get("transaction_type").toString();
                String id = row.get("transaction_id").toString();
                String rawAmount = row.get("amount").toString();
                String date = row.get("transaction_at").toString();
                String status = row.get("status").toString();

                // Format amount display based on type
                String amount = (type.equalsIgnoreCase("Deposit") || type.equalsIgnoreCase("Receive"))
                        ? "+" + rawAmount + " MMK"
                        : "-" + rawAmount + " MMK";

                // Format date & time
                LocalDateTime dateTime = LocalDateTime.parse(date);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssa");
                date = dateTime.format(formatter);

                transactions.add(new Tx(type, id, amount, date, status, "/Resources/user.png"));
            }
        }
        // --- REAL DATA LOGIC END ---

        JPanel transactionContainer = new JPanel();
        transactionContainer.setLayout(new BoxLayout(transactionContainer, BoxLayout.Y_AXIS));
        transactionContainer.setBackground(new Color(30, 127, 179));

        for (Tx tx : transactions) {
            transactionContainer.add(createTransactionItem(tx));
            transactionContainer.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(transactionContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(30, 127, 179));
        
        mainPanel.add(scrollPane);
        return mainPanel;
    }

    private JPanel createTransactionItem(Tx tx) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(173, 216, 230));
        panel.setMaximumSize(new Dimension(1000, 110));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(135, 206, 250), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // User Image
        JLabel userImageLabel = new JLabel();
        userImageLabel.setPreferredSize(new Dimension(70, 70));
        ImageIcon userIcon = loadIcon(tx.userImage, 60, 60);
        if (userIcon != null) userImageLabel.setIcon(new ImageIcon(makeCircular(userIcon.getImage())));
        else userImageLabel.setIcon(createColoredCircleIcon(tx.type));
        panel.add(userImageLabel, BorderLayout.WEST);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row1.setOpaque(false);

        JLabel typeLabel = new JLabel(tx.type);
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        typeLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        typeLabel.setUI(new PillLabelUI(tx.type.equalsIgnoreCase("Deposit") || tx.type.equalsIgnoreCase("Receive") ?
            new Color(60, 179, 113) : new Color(220, 20, 60), 30));
        row1.add(typeLabel);

        JLabel idLabel = new JLabel("Transaction ID:" + tx.transactionId);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        idLabel.setForeground(new Color(50, 50, 50));
        row1.add(idLabel);

        JLabel amountLabel = new JLabel(tx.amount);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        amountLabel.setForeground(tx.amount.startsWith("+") ? new Color(40, 140, 80) : new Color(190, 20, 50));
        row1.add(amountLabel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setOpaque(false);
        JLabel dateLabel = new JLabel(tx.date);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dateLabel.setForeground(new Color(80, 80, 80));
        row2.add(dateLabel);

        JLabel statusLabel = new JLabel(tx.status);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 15, 4, 15));
        statusLabel.setUI(new PillLabelUI(tx.status.equalsIgnoreCase("Success") ? new Color(60, 179, 113) : new Color(220, 20,
                60), 25));
        row2.add(statusLabel);

        infoPanel.add(row1);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(row2);
        panel.add(infoPanel, BorderLayout.CENTER);

        // Click to Detail
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    // TransactionDetail class ထဲမှာ constructor အသစ်ဆောက်ထားဖို့လိုပါတယ်
                    TransactionDetail detail = new TransactionDetail(tx, cardLayout, contentPanel);
                    contentPanel.add(detail, "Detail");
                    cardLayout.show(contentPanel, "Detail");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "TransactionDetail class missing or error!");
                }
            }
            public void mouseEntered(MouseEvent e) { panel.setBackground(new Color(135, 206, 250)); }
            public void mouseExited(MouseEvent e) { panel.setBackground(new Color(173, 216, 230)); }
        });

        return panel;
    }

    private Icon createColoredCircleIcon(String type) {
        int size = 60;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(100, 149, 237));
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(image);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bankify - Transactions");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            CardLayout cl = new CardLayout();
            JPanel cp = new JPanel(cl);
            cp.add(new TransactionsPage(cl, cp, frame), "Transactions");
            frame.add(cp);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}