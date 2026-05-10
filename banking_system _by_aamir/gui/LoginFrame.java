package gui;
import dao.AdminDAO;
import events.LoginEvent;
import interfaces.LoginListener;
import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends JFrame {

    private final AdminDAO adminDAO = new AdminDAO();
    private final List<LoginListener> loginListeners = new ArrayList<>();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Banking Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    public void addLoginListener(LoginListener listener) { loginListeners.add(listener); }
    public void removeLoginListener(LoginListener listener) { loginListeners.remove(listener); }

    private void fireLoginEvent(LoginEvent event) {
        for (LoginListener l : loginListeners) l.onLoginStateChanged(event);
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Banking Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        panel.add(statusLabel, gbc);

        setContentPane(panel);
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Please wait...");

        SwingWorker<Admin, Void> worker = new SwingWorker<>() {
            @Override
            protected Admin doInBackground() {
                return adminDAO.login(username, password);
            }
            @Override
            protected void done() {
                try {
                    Admin admin = get();
                    if (admin != null) {
                        statusLabel.setForeground(new Color(0, 130, 0));
                        statusLabel.setText("Login successful!");
                        fireLoginEvent(new LoginEvent(LoginFrame.this, LoginEvent.Type.SUCCESS, admin));
                        SwingUtilities.invokeLater(() -> {
                            new DashboardFrame(admin).setVisible(true);
                            dispose();
                        });
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Invalid username or password.");
                        passwordField.setText("");
                        fireLoginEvent(new LoginEvent(LoginFrame.this, LoginEvent.Type.FAILED, null));
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        worker.execute();
    }
}
