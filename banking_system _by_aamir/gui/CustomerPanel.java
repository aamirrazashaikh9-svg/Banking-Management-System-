package gui;

import dao.CustomerDAO;
import events.CustomerEvent;
import interfaces.CustomerListener;
import model.Admin;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final Admin currentAdmin;
    private final List<CustomerListener> customerListeners = new ArrayList<>();

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JTextField nameField, cnicField, phoneField;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn;
    private JLabel totalLabel;
    private int selectedCustomerId = -1;

    public CustomerPanel(Admin admin) {
        this.currentAdmin = admin;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildUI();
        loadTable("");
    }

    public void addCustomerListener(CustomerListener listener) { customerListeners.add(listener); }
    public void removeCustomerListener(CustomerListener listener) { customerListeners.remove(listener); }

    private void fireCustomerEvent(CustomerEvent event) {
        for (CustomerListener l : customerListeners) {
            switch (event.getType()) {
                case ADDED:    l.onCustomerAdded(event);    break;
                case UPDATED:  l.onCustomerUpdated(event);  break;
                case DELETED:  l.onCustomerDeleted(event);  break;
                case SELECTED: l.onCustomerSelected(event); break;
                default: break;
            }
        }
    }

    private void buildUI() {
        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(18);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> applyFilter());
        searchPanel.add(searchBtn);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadTable(""); });
        searchPanel.add(refreshBtn);
        totalLabel = new JLabel("Total: 0");
        searchPanel.add(totalLabel);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "CNIC", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    row = table.convertRowIndexToModel(row);
                    selectedCustomerId = (int) tableModel.getValueAt(row, 0);
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                    cnicField.setText((String) tableModel.getValueAt(row, 2));
                    phoneField.setText((String) tableModel.getValueAt(row, 3));
                    Customer c = new Customer(selectedCustomerId, nameField.getText(), cnicField.getText(), phoneField.getText());
                    fireCustomerEvent(new CustomerEvent(this, CustomerEvent.Type.SELECTED, c));
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(18);
        cnicField = new JTextField(18);
        phoneField = new JTextField(18);

        addRow(form, gbc, "Name:", nameField, 0);
        addRow(form, gbc, "CNIC:", cnicField, 1);
        addRow(form, gbc, "Phone:", phoneField, 2);
        outer.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        addBtn    = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn  = new JButton("Clear");

        addBtn.addActionListener(e    -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e  -> clearForm());

        btnPanel.add(addBtn); btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn); btnPanel.add(clearBtn);
        outer.add(btnPanel, BorderLayout.EAST);

        return outer;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, String lbl, JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        p.add(new JLabel(lbl), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(field, gbc);
    }

    private void loadTable(String filter) {
        tableModel.setRowCount(0);
        List<Customer> list = customerDAO.getAllCustomers();
        int count = 0;
        for (Customer c : list) {
            if (filter.isEmpty()
                    || c.getName().toLowerCase().contains(filter.toLowerCase())
                    || c.getCnic().contains(filter)) {
                tableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getCnic(), c.getPhone()});
                count++;
            }
        }
        totalLabel.setText("Total: " + count);
    }

    private void applyFilter() {
        String filter = searchField.getText().trim();
        if (filter.isEmpty()) { sorter.setRowFilter(null); totalLabel.setText("Total: " + tableModel.getRowCount()); return; }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filter, 1, 2));
        totalLabel.setText("Total: " + table.getRowCount());
    }

    private void addCustomer() {
        if (!validateForm()) return;
        Customer c = new Customer(nameField.getText().trim(), cnicField.getText().trim(), phoneField.getText().trim());
        int id = customerDAO.addCustomer(c);
        if (id > 0) {
            c.setCustomerId(id);
            JOptionPane.showMessageDialog(this, "Customer added! ID: " + id);
            clearForm();
            loadTable("");
            fireCustomerEvent(new CustomerEvent(this, CustomerEvent.Type.ADDED, c));
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer. CNIC may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId < 0) { JOptionPane.showMessageDialog(this, "Select a customer to update."); return; }
        if (!validateForm()) return;
        Customer c = new Customer(selectedCustomerId, nameField.getText().trim(), cnicField.getText().trim(), phoneField.getText().trim());
        if (customerDAO.updateCustomer(c)) {
            JOptionPane.showMessageDialog(this, "Customer updated.");
            loadTable("");
            fireCustomerEvent(new CustomerEvent(this, CustomerEvent.Type.UPDATED, c));
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId < 0) { JOptionPane.showMessageDialog(this, "Select a customer to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer '" + nameField.getText() + "'? Their accounts will also be deleted.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Customer c = new Customer(selectedCustomerId, nameField.getText(), cnicField.getText(), phoneField.getText());
            if (customerDAO.deleteCustomer(selectedCustomerId)) {
                JOptionPane.showMessageDialog(this, "Customer deleted.");
                clearForm();
                loadTable("");
                fireCustomerEvent(new CustomerEvent(this, CustomerEvent.Type.DELETED, c));
            }
        }
    }

    private void clearForm() {
        nameField.setText(""); cnicField.setText(""); phoneField.setText("");
        table.clearSelection();
        selectedCustomerId = -1;
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Name is required."); return false; }
        if (cnicField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "CNIC is required."); return false; }
        return true;
    }
}
