package org.petya8bachey.service;

import java.sql.*;

public class AuthService {
    private final DatabaseService dbService;

    public AuthService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public boolean checkUserRole(String username, String role) throws SQLException {
        String sql = "SELECT 1 FROM pg_roles WHERE rolname = ? AND pg_has_role(?, ?::regrole, 'member')";

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role);
            pstmt.setString(2, username);
            pstmt.setString(3, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String determineUserRole(String username) throws SQLException {
        // Проверяем роли в порядке привилегий
        String[] roles = {"trading_admin", "trading_broker", "trading_analyst", "trading_client"};

        for (String role : roles) {
            if (checkUserRole(username, role)) {
                return role;
            }
        }
        return null;
    }
}