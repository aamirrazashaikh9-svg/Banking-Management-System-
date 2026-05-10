package gui;
import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;
import model.Admin;
import model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportPanel extends JPanel {

    private final AccountDAO     accountDAO     = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Admin          currentAdmin;

    public ReportPanel(Admin admin) {
        this.currentAdmin = admin;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildUI();
    }

    private void buildUI() {
        JTabbedPane inner = new JTabbedPane();
        inner.addTab("Summary Report",      buildSummaryTab());
        inner.addTab("Interest Calculator", buildInterestTab());
        add(inner, BorderLayout.CENTER);
    }

    private JPanel buildSummaryTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JButton genBtn = new JButton("Generate Summary Report");
        topBar.add(genBtn);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Metric", "Value"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        genBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<Account>     accounts = accountDAO.getAllAccounts();
            List<Transaction> txList   = transactionDAO.getAllTransactions();

            double totalBal        = accounts.stream().mapToDouble(Account::getBalance).sum();
            double totalDeposited  = txList.stream().filter(t -> t.getType() == Transaction.TransactionType.DEPOSIT).mapToDouble(Transaction::getAmount).sum();
            double totalWithdrawn  = txList.stream().filter(t -> t.getType() == Transaction.TransactionType.WITHDRAWAL).mapToDouble(Transaction::getAmount).sum();
            double totalTransferred= txList.stream().filter(t -> t.getType() == Transaction.TransactionType.TRANSFER).mapToDouble(Transaction::getAmount).sum();
            long deposits          = txList.stream().filter(t -> t.getType() == Transaction.TransactionType.DEPOSIT).count();
            long withdrawals       = txList.stream().filter(t -> t.getType() == Transaction.TransactionType.WITHDRAWAL).count();
            long transfers         = txList.stream().filter(t -> t.getType() == Transaction.TransactionType.TRANSFER).count();
            long savings = accounts.stream().filter(a -> a.getAccountType() == Account.AccountType.SAVINGS).count();
            long current = accounts.stream().filter(a -> a.getAccountType() == Account.AccountType.CURRENT).count();
            long fixed   = accounts.stream().filter(a -> a.getAccountType() == Account.AccountType.FIXED_DEPOSIT).count();

            model.addRow(new Object[]{"Total Accounts",         accounts.size()});
            model.addRow(new Object[]{"Total Balance (PKR)",    String.format("%,.2f", totalBal)});
            model.addRow(new Object[]{"Total Transactions",     txList.size()});
            model.addRow(new Object[]{"Deposits",               deposits + "  (PKR " + String.format("%,.2f", totalDeposited) + ")"});
            model.addRow(new Object[]{"Withdrawals",            withdrawals + "  (PKR " + String.format("%,.2f", totalWithdrawn) + ")"});
            model.addRow(new Object[]{"Transfers",              transfers + "  (PKR " + String.format("%,.2f", totalTransferred) + ")"});
            model.addRow(new Object[]{"SAVINGS Accounts",       savings});
            model.addRow(new Object[]{"CURRENT Accounts",       current});
            model.addRow(new Object[]{"FIXED DEPOSIT Accounts", fixed});
        });

        return panel;
    }

    private JPanel buildInterestTab() {
        JPanel panel = new JPanel(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder("Simple / Compound Interest Calculator"));
        card.setPreferredSize(new Dimension(380, 280));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField principalField = new JTextField(12);
        JTextField rateField      = new JTextField(12);
        JTextField yearsField     = new JTextField(12);

        addRow(card, gbc, "Principal (PKR):", principalField, 0);
        addRow(card, gbc, "Annual Rate (%):", rateField,      1);
        addRow(card, gbc, "Time (Years):",    yearsField,     2);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.4;
        card.add(new JLabel("Interest Type:"), gbc);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Simple Interest", "Compound Interest"});
        gbc.gridx = 1; gbc.weightx = 0.6;
        card.add(typeCombo, gbc);

        JButton calcBtn = new JButton("Calculate");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(12, 12, 4, 12);
        card.add(calcBtn, gbc);

        JLabel resultLabel = new JLabel("-", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 5; gbc.insets = new Insets(4, 12, 2, 12);
        card.add(resultLabel, gbc);

        JLabel totalLabel = new JLabel("", SwingConstants.CENTER);
        gbc.gridy = 6;
        card.add(totalLabel, gbc);

        calcBtn.addActionListener(e -> {
            try {
                double p = Double.parseDouble(principalField.getText().trim());
                double r = Double.parseDouble(rateField.getText().trim());
                double t = Double.parseDouble(yearsField.getText().trim());
                boolean isCompound = typeCombo.getSelectedIndex() == 1;

                if (isCompound) {
                    double total    = p * Math.pow(1 + r / 100.0, t);
                    double interest = total - p;
                    resultLabel.setText(String.format("Compound Interest = PKR %,.2f", interest));
                    totalLabel.setText(String.format("Total Amount = PKR %,.2f", total));
                } else {
                    double interest = (p * r * t) / 100.0;
                    resultLabel.setText(String.format("Simple Interest = PKR %,.2f", interest));
                    totalLabel.setText(String.format("Total Amount = PKR %,.2f", p + interest));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid numeric values.");
            }
        });

        panel.add(card);
        return panel;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, String lbl, JTextField field, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        p.add(new JLabel(lbl), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        p.add(field, gbc);
    }
}
