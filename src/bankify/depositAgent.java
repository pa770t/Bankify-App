package bankify;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class depositAgent extends JFrame {

    private String userName, dateTime, amount;

    public depositAgent(String name, String date, String amount) {
        this.userName = name; this.dateTime = date; this.amount = amount;
        setupFrame();
    }

    public depositAgent() { this("Maung Maung", "2026/01/20 10:30 AM", "100,000 MMK"); }

    private void setupFrame() {
        setTitle("Bankify - Agent Approval");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel contentPanel = createContent();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContent() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 127, 179));
        panel.setLayout(null);

        int pillWidth = 220, pillHeight = 60;
        int pillX = (1200 - pillWidth) / 2, pillY = 80;

        // --- Back Button near Agent Box ---
        JButton btnBack = new JButton();
        try {
            URL backUrl = getClass().getResource("/Resources/exit_arrow.png");
            if (backUrl != null) btnBack.setIcon(new ImageIcon(new ImageIcon(backUrl).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            else btnBack.setText("<-");
        } catch (Exception e) { btnBack.setText("<-"); }
        btnBack.setBounds(pillX - 150, pillY + 5, 50, 50);
        btnBack.setContentAreaFilled(false); btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> { dispose(); new AgentRequestListPage().setVisible(true); });
        panel.add(btnBack);

        // --- Agent Pill ---
        JPanel pillPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 191, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
                g2.dispose();
            }
        };
        pillPanel.setBounds(pillX, pillY, pillWidth, pillHeight);
        pillPanel.setOpaque(false); pillPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
        pillPanel.add(createScaledImageLabel("/Resources/agent.png", 35, 35));
        JLabel lblT = new JLabel("Agent"); lblT.setForeground(Color.WHITE); lblT.setFont(new Font("Tw Cen MT", Font.BOLD, 28));
        pillPanel.add(lblT); panel.add(pillPanel);

        // --- Fields ---
        int cW = 500, cX = (1200 - cW) / 2;
        createReadOnlyField(panel, "User ID", "987654", cX, 180, cW);
        createReadOnlyField(panel, "User Name", userName, cX, 280, cW);
        createReadOnlyField(panel, "Amount", amount, cX, 380, cW);
        createReadOnlyField(panel, "Date & Time", dateTime, cX, 480, cW);

        // --- Accept & Deny Logic ---
        RoundedButton btnAccept = new RoundedButton("Accept");
        btnAccept.setBounds(cX, 610, 220, 60); btnAccept.setBackground(new Color(50, 205, 50));
        btnAccept.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Approve this request?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) { dispose(); new AgentRequestListPage().setVisible(true); }
        });
        panel.add(btnAccept);

        RoundedButton btnDeny = new RoundedButton("Deny");
        btnDeny.setBounds(cX + 280, 610, 220, 60); btnDeny.setBackground(new Color(220, 20, 60));
        btnDeny.addActionListener(e -> {
            int res = JOptionPane.showConfirmDialog(this, "Deny this request?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) { dispose(); new AgentRequestListPage().setVisible(true); }
        });
        panel.add(btnDeny);

        return panel;
    }

    private void createReadOnlyField(JPanel panel, String label, String val, int x, int y, int w) {
        JLabel lbl = new JLabel(label); lbl.setForeground(Color.WHITE); lbl.setFont(new Font("Tw Cen MT", Font.BOLD, 20));
        lbl.setBounds(x, y, w, 30); panel.add(lbl);
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); g2.dispose();
            }
        };
        p.setOpaque(false); p.setBounds(x, y + 35, w, 50); p.setLayout(null);
        JLabel vL = new JLabel(val); vL.setFont(new Font("Tw Cen MT", Font.BOLD, 22)); vL.setBounds(20, 0, w - 40, 50);
        p.add(vL); panel.add(p);
    }

    private JLabel createScaledImageLabel(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url != null) return new JLabel(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        return new JLabel();
    }

    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text); setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE); setFont(new Font("Tw Cen MT", Font.BOLD, 22)); setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new depositAgent().setVisible(true)); }
}