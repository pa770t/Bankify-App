package bankify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AgentRequestListPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private List<RequestItem> requestList;
    private JPanel listContainer;
    private JButton btnSort;

    public AgentRequestListPage() {
        setTitle("Bankify - Request List");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initData();
        JPanel contentPanel = createBaseLayout();
        add(contentPanel, BorderLayout.CENTER);
        
        refreshList();
    }

    private void initData() {
        requestList = new ArrayList<>();
        // Amount Data များ ထည့်သွင်းထားပါသည်
        requestList.add(new RequestItem("Maung Maung", "2026/01/20 10:30 AM", "100,000 MMK"));
        requestList.add(new RequestItem("Aung Aung",   "2026/01/20 11:15 AM", "50,000 MMK"));
        requestList.add(new RequestItem("Su Su",       "2026/01/19 04:45 PM", "250,000 MMK"));
        requestList.add(new RequestItem("Kyaw Kyaw",   "2026/01/19 02:20 PM", "10,000 MMK"));
    }

    private JPanel createBaseLayout() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 127, 179));
        panel.setLayout(null);

        int pillWidth = 220; 
        int pillHeight = 60;
        int pillX = (1200 - pillWidth) / 2;
        int pillY = 80;

        JPanel pillPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 191, 255)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), pillHeight, pillHeight);
                g2.dispose();
            }
        };
        pillPanel.setBounds(pillX, pillY, pillWidth, pillHeight);
        pillPanel.setOpaque(false);
        pillPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));

        JLabel lblAgentIcon = createScaledImageLabel("/Resources/agent.png", 35, 35); 
        pillPanel.add(lblAgentIcon);

        JLabel lblAgentText = new JLabel("Agent");
        lblAgentText.setForeground(Color.WHITE);
        lblAgentText.setFont(new Font("Tw Cen MT", Font.BOLD, 28));
        pillPanel.add(lblAgentText);
        panel.add(pillPanel);

        btnSort = new JButton("Sort By") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 191, 255)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); 
                g2.dispose();
                super.paintComponent(g);
            }
        };

        try {
            URL iconURL = getClass().getResource("/Resources/dropdown.png"); 
            if (iconURL != null) {
                ImageIcon sortIcon = new ImageIcon(new ImageIcon(iconURL).getImage()
                                     .getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                btnSort.setIcon(sortIcon);
                btnSort.setIconTextGap(10);
            }
        } catch (Exception e) {}

        btnSort.setBounds(900, 85, 160, 45); 
        btnSort.setForeground(Color.WHITE);
        btnSort.setFont(new Font("Tw Cen MT", Font.BOLD, 17));
        btnSort.setContentAreaFilled(false);
        btnSort.setBorderPainted(false);
        btnSort.setFocusPainted(false);
        btnSort.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnSort.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPopupMenu sortMenu = new JPopupMenu();
        JMenuItem itemDate = new JMenuItem("Sort by Date (Newest)");
        JMenuItem itemName = new JMenuItem("Sort by Name (A-Z)");
        itemDate.addActionListener(e -> sortByDate());
        itemName.addActionListener(e -> sortByName());
        sortMenu.add(itemDate);
        sortMenu.add(itemName);

        btnSort.addActionListener(e -> sortMenu.show(btnSort, 0, btnSort.getHeight()));
        panel.add(btnSort);

        listContainer = new JPanel();
        listContainer.setBounds(0, 180, 1200, 600);
        listContainer.setOpaque(false);
        listContainer.setLayout(null);
        panel.add(listContainer);

        return panel;
    }

    private void sortByName() {
        Collections.sort(requestList, Comparator.comparing(item -> item.name));
        btnSort.setText("By Name");
        refreshList();
    }

    private void sortByDate() {
        Collections.sort(requestList, (o1, o2) -> o2.dateTimeObj.compareTo(o1.dateTimeObj));
        btnSort.setText("By Date");
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAll();
        int startY = 10;
        for (RequestItem item : requestList) {
            // createRequestItem ဆီသို့ amount ပါ ပို့ပေးလိုက်ပါသည်
            JPanel itemPanel = createRequestItem(item.name, item.dateStr, item.amount, 600, 90);
            itemPanel.setBounds(300, startY, 600, 90);
            
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose();
                    // depositAgent constructor တွင် amount ပါ ထည့်ပေးရပါမည်
                    new depositAgent(item.name, item.dateStr, item.amount).setVisible(true);
                }
            });
            listContainer.add(itemPanel);
            startY += 110;
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createRequestItem(String name, String date, String amount, int width, int height) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
                
                // --- Green Amount Pill (ညာဘက်အစွန်) ---
                int amPillW = 140;
                int amPillH = 40;
                int amX = getWidth() - amPillW - 30;
                int amY = (getHeight() - amPillH) / 2;
                g2.setColor(new Color(50, 205, 50)); // Green Color
                g2.fillRoundRect(amX, amY, amPillW, amPillH, amPillH, amPillH);
                
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(null);
        
        JLabel lblIcon = createScaledImageLabel("/Resources/my_profile.png", 50, 50);
        lblIcon.setBounds(20, 20, 50, 50);
        p.add(lblIcon);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        lblName.setBounds(90, 15, 300, 30);
        p.add(lblName);

        JLabel lblDate = new JLabel(date);
        lblDate.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        lblDate.setForeground(Color.GRAY);
        lblDate.setBounds(90, 48, 300, 25);
        p.add(lblDate);

        // --- Amount Text အစိမ်းရောင်အကွက်ပေါ်တင်ရန် ---
        JLabel lblAmount = new JLabel(amount, SwingConstants.CENTER);
        lblAmount.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setBounds(600 - 140 - 30, (90-40)/2, 140, 40);
        p.add(lblAmount);

        return p;
    }

    private JLabel createScaledImageLabel(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) return new JLabel(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        return new JLabel();
    }

    class RequestItem {
        String name;
        String dateStr;
        String amount;
        LocalDateTime dateTimeObj;
        public RequestItem(String name, String dateStr, String amount) {
            this.name = name;
            this.dateStr = dateStr;
            this.amount = amount;
            try {
                this.dateTimeObj = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm a"));
            } catch (Exception e) { this.dateTimeObj = LocalDateTime.MIN; }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgentRequestListPage().setVisible(true));
    }
}