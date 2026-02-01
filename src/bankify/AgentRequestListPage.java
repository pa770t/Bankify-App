package bankify;

import bankify.dao.AgentDao;
import bankify.service.PageGuardService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import bankify.dao.MoneyRequestDao;
import bankify.dao.MoneyRequestDao.RequestItem;

public class AgentRequestListPage extends JFrame {

    private static final long serialVersionUID = 1L;
    private List<RequestItem> requestList;
    private JPanel listContainer;
    private JButton btnSort;
    private static Agent agent;
    private static AgentDao agentDao;
    MoneyRequestDao moneyRequestDao = new MoneyRequestDao(DBConnection.getConnection());

    public AgentRequestListPage(Agent agent, AgentDao agentDao) {
        if (agent == null) {
            PageGuardService.checkSession(this, agent);
            return;
        }

        this.agent = agent;
        this.agentDao = agentDao;

        setTitle("Bankify - Request List");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initData();
        JPanel contentPanel = createBaseLayout();
        add(contentPanel, BorderLayout.CENTER);
        
        refreshList(); // to be implemented
    }

    private void initData() {
        requestList = moneyRequestDao.getMoneyRequests(agent.getEmail());
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

        JButton btnRefresh = new JButton("Refresh") {
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
            URL iconURL = getClass().getResource("/Resources/refresh.png");
            if (iconURL != null) {
                ImageIcon refreshIcon = new ImageIcon(new ImageIcon(iconURL).getImage()
                        .getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                btnRefresh.setIcon(refreshIcon);
                btnRefresh.setIconTextGap(10);
            }
        } catch (Exception e) {}

        btnRefresh.setBounds(800, 85, 160, 45);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Tw Cen MT", Font.BOLD, 17));
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRefresh.addActionListener(e -> refreshList());

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
        panel.add(btnRefresh, btnSort);

        listContainer = new JPanel();
        listContainer.setBounds(0, 180, 1200, 600);
        listContainer.setOpaque(false);
        listContainer.setLayout(null);
        panel.add(listContainer);

        return panel;
    }

    private void sortByName() {
        Collections.sort(requestList, Comparator.comparing(item -> item.customerName));
        btnSort.setText("By Name");
        refreshList();
    }

    private void sortByDate() {
        Collections.sort(requestList, (o1, o2) -> o2.requested_at.compareTo(o1.requested_at));
        btnSort.setText("By Date");
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAll();
        requestList = moneyRequestDao.getMoneyRequests(agent.getEmail());
        int startY = 10;
        for (RequestItem item : requestList) {
            // createRequestItem ဆီသို့ amount ပါ ပို့ပေးလိုက်ပါသည်
            JPanel itemPanel = createRequestItem(item.customerName, item.requested_at, item.amount, item.request_type, 600, 90);
            itemPanel.setBounds(300, startY, 600, 90);
            
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose();
                    // depositAgent constructor တွင် amount ပါ ထည့်ပေးရပါမည်
//                    new depositAgent(item.customerName, item.requested_at, String.format("%,.0f", item.amount)).setVisible(true);
                    new depositAgent(item, agent, agentDao).setVisible(true);
                }
            });
            listContainer.add(itemPanel);
            startY += 110;
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createRequestItem(String name, String date, double amount, String requestType, int width,
                                     int height) {
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
                if (requestType.equals("DEPOSIT")) {
                    g2.setColor(new Color(201, 38, 38)); // Red Color
                } else if (requestType.equals("WITHDRAW")) {
                    g2.setColor(new Color(50, 205, 50)); // Green Color
                }
                g2.fillRoundRect(amX, amY, amPillW, amPillH, amPillH, amPillH);
                
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(null);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblIcon = createScaledImageLabel("/Resources/my_profile.png", 50, 50);
        lblIcon.setBounds(20, 20, 50, 50);
        p.add(lblIcon);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        lblName.setBounds(90, 15, 300, 30);
        p.add(lblName);

        JLabel lblDate = new JLabel(String.valueOf(date));
        lblDate.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        lblDate.setForeground(Color.GRAY);
        lblDate.setBounds(90, 48, 300, 25);
        p.add(lblDate);

        // --- Amount Text အစိမ်းရောင်အကွက်ပေါ်တင်ရန် ---
        JLabel lblAmount = new JLabel(String.valueOf(amount), SwingConstants.CENTER);
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

    public static void launch(Agent agent, AgentDao agentDao) {
        if (agent == null) {
            new AgentLogin().setVisible(true);
        } else {
            new AgentRequestListPage(agent, agentDao).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> AgentRequestListPage.launch(agent, agentDao));
    }
}