package gui;
import dao.TransactionDAO;
import events.TransactionEvent;
import interfaces.TransactionListener;
import model.Admin;
import model.Transaction;
import model.Transaction.TransactionType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionPanel extends JPanel {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Admin currentAdmin;
    private final List<TransactionListener> transactionListeners = new ArrayList<>();

    private JComboBox<String> operationCombo;
    private JTextField fromAccountField, toAccountField, amountField;
    private JPanel toPanel;
    private JLabel resultLabel;

    public TransactionPanel(Admin admin) {
        this.currentAdmin = admin;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildUI();
    }

    public void addTransactionListener(TransactionListener listener)    { transactionListeners.add(listener); }
    public void removeTransactionListener(TransactionListener listener) { transactionListeners.remove(listener); }
    public void addTransactionListenerExternal(TransactionListener listener) { addTransactionListener(listener); }

    private void fireTransactionEvent(TransactionEvent event) {
        for (TransactionListener l : transactionListeners) {
            if (event.getType() == TransactionEvent.Type.FAILED)
                l.onTransactionFailed(event);
            else
                l.onTransactionCompleted(event);
        }
    }

    private void buildUI() {
        JPanel center = new JPanel(new GridBagLayout());
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder("Process Transaction"));
        card.setPreferredSize(new Dimension(400, 280));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        card.add(new JLabel("Operation:"), gbc);
        operationCombo = new JComboBox<>(new String[]{"Deposit", "Withdrawal", "Transfer"});
        operationCombo.addActionListener(e -> toggleTransferField());
        gbc.gridx = 1; gbc.weightx = 0.6;
        card.add(operationCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.4;
        card.add(new JLabel("Account ID:"), gbc);
        fromAccountField = new JTextField(12);
        gbc.gridx = 1; gbc.weightx = 0.6;
        card.add(fromAccountField, gbc);

        toPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        toPanel.add(new JLabel("To Account ID:"));
        toAccountField = new JTextField(12);
        toPanel.add(toAccountField);
        toPanel.setVisible(false);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        card.add(toPanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.4;
        card.add(new JLabel("Amount (PKR):"), gbc);
        amountField = new JTextField(12);
        gbc.gridx = 1; gbc.weightx = 0.6;
        card.add(amountField, gbc);

        JButton submitBtn = new JButton("Submit Transaction");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(14, 10, 4, 10);
        card.add(submitBtn, gbc);

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        gbc.gridy = 5; gbc.insets = new Insets(4, 10, 4, 10);
        card.add(resultLabel, gbc);

        submitBtn.addActionListener(e -> processTransaction());
        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void toggleTransferField() {
        toPanel.setVisible("Transfer".equals(operationCombo.getSelectedItem()));
        revalidate(); repaint();
    }

    private void processTransaction() {
        String operation = (String) operationCombo.getSelectedItem();
        int fromId;
        double amount;

        try {
            fromId = Integer.parseInt(fromAccountField.getText().trim());
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Enter a valid Account ID and positive amount.");
            return;
        }

        Transaction result = null;
        TransactionEvent.Type eventType;

        switch (operation) {
            case "Deposit":
                result    = transactionDAO.deposit(fromId, amount, currentAdmin.getAdminId());
                eventType = result != null ? TransactionEvent.Type.DEPOSIT : TransactionEvent.Type.FAILED;
                break;
            case "Withdrawal":
                result    = transactionDAO.withdraw(fromId, amount, currentAdmin.getAdminId());
                eventType = result != null ? TransactionEvent.Type.WITHDRAWAL : TransactionEvent.Type.FAILED;
                break;
            case "Transfer":
                int toId;
                try { toId = Integer.parseInt(toAccountField.getText().trim()); }
                catch (NumberFormatException e) {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("Enter a valid destination Account ID.");
                    return;
                }
                if (toId == fromId) {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("Cannot transfer to the same account.");
                    return;
                }
                result    = transactionDAO.transfer(fromId, toId, amount, currentAdmin.getAdminId());
                eventType = result != null ? TransactionEvent.Type.TRANSFER : TransactionEvent.Type.FAILED;
                break;
            default: return;
        }

        if (result != null) {
            resultLabel.setForeground(new Color(0, 130, 0));
            resultLabel.setText(operation + " successful! Transaction ID: " + result.getTransactionId());
            clearForm();
            fireTransactionEvent(new TransactionEvent(this, eventType, result,
                    operation + " of PKR " + String.format("%.2f", amount) + " processed."));
        } else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Transaction failed. Check account ID or balance.");
            fireTransactionEvent(new TransactionEvent(this, TransactionEvent.Type.FAILED, null,
                    operation + " failed for account #" + fromId));
        }
    }

    private void clearForm() {
        fromAccountField.setText("");
        toAccountField.setText("");
        amountField.setText("");
    }
}
