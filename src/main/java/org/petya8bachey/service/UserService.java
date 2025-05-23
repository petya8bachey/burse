package org.petya8bachey.service;

import org.petya8bachey.model.Client;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class UserService {
    private final DatabaseService dbService;

    public UserService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public Optional<Client> authenticateClient(String taxId) throws SQLException {
        String sql = "SELECT * FROM Client WHERE tax_id = ?";

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, taxId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Properly handle timestamp conversion
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");

                    return Optional.of(new Client(
                            rs.getInt("client_id"),
                            rs.getString("full_name"),
                            rs.getString("tax_id"),
                            rs.getString("client_type"),
                            rs.getDate("registration_date").toLocalDate(),
                            rs.getInt("broker_id"),
                            createdAt != null ? createdAt.toInstant() : null,
                            updatedAt != null ? updatedAt.toInstant() : null
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void updateCurrentClient(int clientId) throws SQLException {
        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET app.current_client_id = " + clientId);
        }
    }
}