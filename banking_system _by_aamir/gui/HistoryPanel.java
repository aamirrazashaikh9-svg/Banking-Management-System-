package gui;

import dao.TransactionDAO;
import events.TransactionEvent;
import interfaces.TransactionListener;
import model.Admin;
import model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryPanel extends JPanel implements TransactionListener {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Admin currentAdmin;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField accountFilterField;
    private JLabel countLabel;

    public HistoryPanel(Admin admin) {
        this.currentAdmin = admin;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildUI();
        loadAll();
    }

    @Override
    public void onTransactionCompleted(TransactionEvent event) {
        SwingUtilities.invokeLater(() -> {
            loadAll();
            countLabel.setText("Refreshed - new " + event.getType() + " recorded.");
        });
    }

    @Override
    public void onTransactionFailed(TransactionEvent event) {
        SwingUtilities.invokeLater(() -> countLabel.setText("A transaction failed and was not recorded."));
    }

    private void buildUI() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by Account ID:"));
        accountFilterField = new JTextField(10);
        filterPanel.add(accountFilterField);

        JButton filterBtn = new JButton("Search");
        JButton allBtn    = new JButton("Show All");
        filterBtn.addActionListener(e -> loadByAccount());
        allBtn.addActionListener(e    -> { accountFilterField.setText(""); loadAll(); });
        filterPanel.add(filterBtn);
        filterPanel.add(allBtn);

        countLabel = new JLabel("Transactions: 0");
        filterPanel.add(countLabel);
        add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"TX ID", "Type", "From Acc.", "To Acc.", "Amount (PKR)", "Date & Time", "Admin"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        List<Transaction> list = transactionDAO.getAllTransactions();
        for (Transaction t : list) addRow(t);
        countLabel.setText("Transactions: " + list.size());
    }

    private void loadByAccount() {
        String text = accountFilterField.getText().trim();
        if (text.isEmpty()) { loadAll(); return; }
        try {
            int accountId = Integer.parseInt(text);
            tableModel.setRowCount(0);
            List<Transaction> list = transactionDAO.getTransactionsByAccount(accountId);
            for (Transaction t : list) addRow(t);
            countLabel.setText("Transactions: " + list.size() + " for Account #" + accountId);
            if (list.isEmpty())
                JOptionPane.showMessageDialog(this, "No transactions found for Account #" + accountId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid Account ID.");
        }
    }

    private void addRow(Transaction t) {
        tableModel.addRow(new Object[]{
            t.getTransactionId(),
            t.getType(),
            t.getFromAccountId() == 0 ? "-" : t.getFromAccountId(),
            t.getToAccountId()   == 0 ? "-" : t.getToAccountId(),
            String.format("%.2f", t.getAmount()),
            t.getDateTime().toString().replace("T", "  "),
            t.getAdminId()
        });
    }
}
