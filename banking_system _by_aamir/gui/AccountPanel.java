package gui;

import dao.AccountDAO;
import events.AccountEvent;
import interfaces.AccountListener;
import model.Account;
import model.Account.AccountType;
import model.Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AccountPanel extends JPanel {

    private final AccountDAO accountDAO = new AccountDAO();
    private final Admin currentAdmin;
    private final List<AccountListener> accountListeners = new ArrayList<>();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField customerIdField;
    private JComboBox<AccountType> typeCombo;
    private JLabel totalAccountsLabel, totalBalanceLabel;

    public AccountPanel(Admin admin) {
        this.currentAdmin = admin;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildUI();
        loadTable();
    }

    public void addAccountListener(AccountListener listener) { accountListeners.add(listener); }
    public void removeAccountListener(AccountListener listener) { accountListeners.remove(listener); }

    private void fireAccountEvent(AccountEvent event) {
        for (AccountListener l : accountListeners) {
            switch (event.getType()) {
                case OPENED:          l.onAccountOpened(event);   break;
                case CLOSED:          l.onAccountClosed(event);   break;
                case BALANCE_UPDATED: l.onBalanceUpdated(event);  break;
                default: break;
            }
        }
    }

    private void buildUI() {

        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        statsBar.setBorder(BorderFactory.createEtchedBorder());
        totalAccountsLabel = new JLabel("Accounts: 0");
        totalBalanceLabel  = new JLabel("Total Balance: PKR 0.00");
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTable());
        statsBar.add(totalAccountsLabel);
        statsBar.add(new JSeparator(SwingConstants.VERTICAL));
        statsBar.add(totalBalanceLabel);
        statsBar.add(refreshBtn);
        add(statsBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Account ID", "Customer ID", "Type", "Balance (PKR)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c < 2 ? Integer.class : String.class; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        add(buildForm(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Open New Account"));

        formPanel.add(new JLabel("Customer ID:"));
        customerIdField = new JTextField(10);
        formPanel.add(customerIdField);

        formPanel.add(new JLabel("Account Type:"));
        typeCombo = new JComboBox<>(AccountType.values());
        formPanel.add(typeCombo);

        JButton openBtn = new JButton("Open Account");
        openBtn.addActionListener(e -> openAccount());
        formPanel.add(openBtn);

        JButton closeBtn = new JButton("Close Selected");
        closeBtn.addActionListener(e -> closeAccount());
        formPanel.add(closeBtn);

        return formPanel;
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<Account> list = accountDAO.getAllAccounts();
        double totalBal = 0;
        for (Account a : list) {
            tableModel.addRow(new Object[]{
                a.getAccountId(), a.getCustomerId(),
                a.getAccountType(), String.format("%.2f", a.getBalance())
            });
            totalBal += a.getBalance();
        }
        totalAccountsLabel.setText("Accounts: " + list.size());
        totalBalanceLabel.setText("Total Balance: PKR " + String.format("%,.2f", totalBal));
    }

    private void openAccount() {
        String idText = customerIdField.getText().trim();
        if (idText.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a Customer ID."); return; }
        try {
            int id = Integer.parseInt(idText);
            AccountType type = (AccountType) typeCombo.getSelectedItem();
            Account acc = new Account(id, type);
            int newId = accountDAO.addAccount(acc);
            if (newId > 0) {
                acc.setAccountId(newId);
                JOptionPane.showMessageDialog(this, "Account opened! Account ID: " + newId);
                customerIdField.setText("");
                loadTable();
                fireAccountEvent(new AccountEvent(this, AccountEvent.Type.OPENED, acc));
            } else {
                JOptionPane.showMessageDialog(this, "Failed to open account. Customer ID may not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric Customer ID.");
        }
    }

    private void closeAccount() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an account to close."); return; }
        row = table.convertRowIndexToModel(row);
        int accountId = (int) tableModel.getValueAt(row, 0);
        int custId    = (int) tableModel.getValueAt(row, 1);
        String type   = tableModel.getValueAt(row, 2).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Close Account #" + accountId + "? This cannot be undone.",
                "Confirm Close", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && accountDAO.deleteAccount(accountId)) {
            JOptionPane.showMessageDialog(this, "Account closed.");
            Account dummy = new Account(accountId, custId, AccountType.valueOf(type), 0);
            loadTable();
            fireAccountEvent(new AccountEvent(this, AccountEvent.Type.CLOSED, dummy));
        }
    }
}
