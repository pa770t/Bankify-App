package bankify.admin;

import bankify.Agent;
import bankify.dao.AgentDao;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class AdminSecurityPage extends JFrame {

    private List<SecurityLog> logs = new ArrayList<>();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private static Connection conn;
    private static Agent agent;
    private static AgentDao agentDao;

    public AdminSecurityPage(Agent ag, AgentDao agd, Connection connection) {
        agent = ag;
        agentDao = agd;
        conn = connection;

        setTitle("Bankify - Security Management");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initSampleData();
        initUI();
    }

    private void initUI() {
        AdminSidebar sidebar = new AdminSidebar(this, "Security", agent, agentDao, conn);
        add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(new Color(240, 242, 245));

        contentPanel.add(createHeader(), BorderLayout.NORTH);
        contentPanel.add(createToolbar(), BorderLayout.CENTER);
        contentPanel.add(createTablePanel(), BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= Header =================
    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 127, 179));
        panel.setPreferredSize(new Dimension(0, 110));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Security Management");
        title.setFont(new Font("Tw Cen MT", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Monitor system security activities & logs");
        subtitle.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
        subtitle.setForeground(new Color(220, 220, 220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20)); // ⬆️ move text up
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);

        return panel;
    }

    // ================= Toolbar =================
    private JPanel createToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // ===== Search Pane =====
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        searchPanel.setPreferredSize(new Dimension(300, 40));

        searchField = new JTextField();
        searchField.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // ===== Search Button =====
        JButton searchBtn = new JButton("🔍");
        searchBtn.setPreferredSize(new Dimension(45, 40));
        searchBtn.setBackground(new Color(30, 127, 179));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createEmptyBorder());
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchBtn.addActionListener(e -> searchLogs());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        // ===== Filter Combo =====
        filterCombo = new JComboBox<>(new String[]{
                "All", "Login Success", "Login Failed", "Logout", "Blocked"
        });
        filterCombo.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
        filterCombo.setPreferredSize(new Dimension(160, 40));
        filterCombo.addActionListener(e -> filterLogs());

        // ===== Buttons =====
        JButton exportBtn = new RoundedButton("Export", new Color(39, 174, 96));
        exportBtn.setPreferredSize(new Dimension(120, 40));
        exportBtn.addActionListener(e -> exportLogs());

        JButton clearBtn = new RoundedButton("Clear Logs", new Color(231, 76, 60));
        clearBtn.setPreferredSize(new Dimension(140, 40));
        clearBtn.addActionListener(e -> clearLogs());

        // ===== Add Components =====
        panel.add(searchPanel);
        panel.add(filterCombo);
        panel.add(exportBtn);
        panel.add(clearBtn);

        return panel;
    }

    // ================= Table =================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        String[] cols = {"ID", "Username", "Action", "Date & Time", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);

        table.setRowHeight(45);
        table.setFont(new Font("Tw Cen MT", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 127, 179));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Tw Cen MT", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(0, 45));

        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        populateTable();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ================= Logic =================
    private void populateTable() {
        tableModel.setRowCount(0);
        for (SecurityLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.id, log.username, log.action, log.datetime, log.status
            });
        }
    }

    private void searchLogs() {
        String text = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);

        for (SecurityLog log : logs) {
            if (log.username.toLowerCase().contains(text) ||
                log.action.toLowerCase().contains(text)) {

                tableModel.addRow(new Object[]{
                        log.id, log.username, log.action, log.datetime, log.status
                });
            }
        }
    }

    private void filterLogs() {
        String filter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);

        for (SecurityLog log : logs) {
            if (filter.equals("All") || log.action.equals(filter)) {
                tableModel.addRow(new Object[]{
                        log.id, log.username, log.action, log.datetime, log.status
                });
            }
        }
    }

    private void clearLogs() {
        int c = JOptionPane.showConfirmDialog(this,
                "Clear all security logs?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (c == JOptionPane.YES_OPTION) {
            logs.clear();
            populateTable();
        }
    }

    private void exportLogs() {
        JOptionPane.showMessageDialog(this,
                "Export feature connected (CSV)",
                "Export",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= Sample Data =================
    private void initSampleData() {
        logs.add(new SecurityLog(1, "John Doe", "Login Success", "2024-03-01 09:30", "Success"));
        logs.add(new SecurityLog(2, "Jane Smith", "Login Failed", "2024-03-01 10:00", "Failed"));
        logs.add(new SecurityLog(3, "Alice Brown", "Blocked", "2024-03-02 14:20", "Blocked"));
        logs.add(new SecurityLog(4, "Charlie Wilson", "Logout", "2024-03-02 15:10", "Success"));
        
    }

    // ================= Renderer =================
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);
            setForeground(Color.WHITE);

            switch (value.toString()) {
                case "Success":
                    setBackground(new Color(39, 174, 96));
                    break;
                case "Failed":
                    setBackground(new Color(231, 76, 60));
                    break;
                case "Blocked":
                    setBackground(new Color(127, 140, 141));
                    break;
                default:
                    setBackground(Color.LIGHT_GRAY);
            }
            return this;
        }
    }

    // ================= Button =================
    class RoundedButton extends JButton {
        Color color;

        RoundedButton(String text, Color c) {
            super(text);
            color = c;
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Tw Cen MT", Font.BOLD, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color.darker());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
            g2.dispose();
        }
    }

    // ================= Model =================
    class SecurityLog {
        int id;
        String username, action, datetime, status;

        SecurityLog(int i, String u, String a, String d, String s) {
            id = i;
            username = u;
            action = a;
            datetime = d;
            status = s;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminSecurityPage page = new AdminSecurityPage(agent, agentDao, conn);
            page.setVisible(true);
        });
    }
}
