package gui;
import events.LoginEvent;
import interfaces.LoginListener;
import model.Admin;
import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame implements LoginListener {

    private final Admin currentAdmin;

    public DashboardFrame(Admin admin) {
        this.currentAdmin = admin;
        setTitle("Banking Management System - " + admin.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        buildUI();
    }

    @Override
    public void onLoginStateChanged(LoginEvent event) {
        if (event.getType() == LoginEvent.Type.LOGOUT) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel("Banking Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        header.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userLabel = new JLabel("User: " + currentAdmin.getUsername() + "  |  Role: " + currentAdmin.getRole());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> handleLogout());
        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);
        header.add(rightPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        CustomerPanel customerPanel = new CustomerPanel(currentAdmin);
        AccountPanel accountPanel = new AccountPanel(currentAdmin);
        TransactionPanel transactionPanel = new TransactionPanel(currentAdmin);
        HistoryPanel historyPanel = new HistoryPanel(currentAdmin);

        transactionPanel.addTransactionListenerExternal(historyPanel);

        tabs.addTab("Customers", customerPanel);
        tabs.addTab("Accounts", accountPanel);
        tabs.addTab("Transactions", transactionPanel);
        tabs.addTab("History", historyPanel);

        if (currentAdmin.getRole() == Admin.Role.ADMIN) {
            tabs.addTab("Reports", new ReportPanel(currentAdmin));
        }

        add(tabs, BorderLayout.CENTER);

        // Status bar
        JLabel statusBar = new JLabel("  Ready  |  " + currentAdmin.getRole() + " access");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            onLoginStateChanged(new LoginEvent(this, LoginEvent.Type.LOGOUT, currentAdmin));
        }
    }
}
