package org.petya8bachey.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

public class DatabaseService implements AutoCloseable {
    private Connection connection;
    private final Properties properties;
    private String currentUsername;
    private String currentPassword;

    public DatabaseService() throws IOException {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
        }
    }

    public void setCredentials(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (currentUsername == null || currentPassword == null) {
                // Fallback to default credentials if none provided
                currentUsername = properties.getProperty("db.username");
                currentPassword = properties.getProperty("db.password");
            }

            String url = properties.getProperty("db.url");
            connection = DriverManager.getConnection(url, currentUsername, currentPassword);
            connection.setSchema(properties.getProperty("app.schema"));

            // Set application context for row-level security
            try (var stmt = connection.createStatement()) {
                stmt.execute("SET app.current_broker_id = " + properties.getProperty("app.current_broker_id"));
                stmt.execute("SET app.current_client_id = " + properties.getProperty("app.current_client_id"));
            }
        }
        return connection;
    }

    public void withTransaction(Consumer<Connection> operation) throws SQLException {
        Connection conn = getConnection();
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            operation.accept(conn);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}