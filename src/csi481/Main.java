package csi481;

import csi481.db.DBConnection;
import csi481.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point. Confirms the database is reachable before showing the main
 * window, so a misconfigured connection produces one clear dialog instead
 * of a cascade of exceptions the first time any screen touches the
 * database.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (!DBConnection.testConnection()) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to the database.\n\n"
                                + "Check that MySQL/MariaDB is running and that the connection\n"
                                + "details hard-coded in csi481.db.DBConnection are correct\n"
                                + "(host, port, database name, username and password).",
                        "Database Connection Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
