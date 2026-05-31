package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * MySQL connection helper.
 *
 * Connections are created here only — DAOs receive a Connection as their first
 * argument, and Services own the transaction lifecycle (commit / rollback / close).
 *
 * Prerequisite: an SSH tunnel must be running on local port 3307.
 *   ssh -fN -L 3307:10.0.0.18:3306 ubuntu@134.185.99.179
 * See docs/team-db-setup.md.
 */
public class DBConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static final String url;
    private static final String user;
    private static final String password;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "db.properties not found in classpath. " +
                        "Copy db.properties.example to db.properties and fill in your password.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load db.properties", e);
        }

        url = required(props, "db.url");
        user = required(props, "db.user");
        password = required(props, "db.password");
    }

    private DBConnection() {
        // utility class
    }

    /**
     * Open a new MySQL connection.
     * Caller (Service layer) is responsible for commit / rollback / close.
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String required(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }
}
