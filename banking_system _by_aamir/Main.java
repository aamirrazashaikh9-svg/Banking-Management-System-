import gui.LoginFrame;
import javax.swing.*;
import util.DBConnection;

public class Main{
    public static void main(String[] args) {

        try {
            DBConnection.getConnection();
            System.out.println("[Main] Database connection successful.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to the database.\n\n"
                    + "Please check:\n"
                    + "  • MySQL server is running\n"
                    + "  • Credentials in util/DBConnection.java are correct\n"
                    + "  • banking_db.sql has been executed\n\n"
                    + "Error: " + e.getMessage(),
                    "Database Connection Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.closeConnection();
            System.out.println("[Main] Application shut down cleanly.");
        }));
    }
}
