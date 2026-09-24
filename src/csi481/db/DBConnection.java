package csi481.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central point for obtaining a JDBC connection to the immunisation
 * database.
 *
 * The database username and password are hard-coded here deliberately, per
 * the submission requirement in Assignment 2: "with the database
 * username/password hard-coded to allow running it from the lecturer's
 * side without having to log on." This is explicitly acknowledged as poor
 * general practice (see the comment in the lecture's Example2.java, which
 * makes the same point) and is done here only because the brief requires
 * it for marking purposes, not as a recommended pattern.
 *
 * EDIT THESE THREE CONSTANTS to match the environment the application will
 * actually be run on before packaging the final JAR for submission.
 */
public final class DBConnection {

    // ---- Hard-coded connection parameters: EDIT BEFORE SUBMISSION -------
    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "csi481_immunisation";
    private static final String USER = "csi481app";
    private static final String PASSWORD = "Csi481Pass!";
    // -----------------------------------------------------------------------

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private DBConnection() {
        // Utility class: no instances.
    }

    /**
     * Opens a fresh connection to the database. The caller is responsible
     * for closing it (try-with-resources is used throughout the DAO
     * layer).
     *
     * The MySQL/MariaDB Connector/J driver (JDBC 4.0+) auto-registers
     * itself via META-INF/services once its JAR is on the classpath, so no
     * explicit Class.forName(...) call is required here, unlike the older
     * JDBC examples from the lecture slides.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Quick connectivity check used by the UI at startup so the user gets
     * an immediate, readable error dialog instead of a stack trace buried
     * behind the first button click.
     */
    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
