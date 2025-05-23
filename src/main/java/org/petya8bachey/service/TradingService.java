package org.petya8bachey.service;

import org.petya8bachey.model.*;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TradingService {
    private final DatabaseService dbService;

    public TradingService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    public List<Stock> getAllStocks() throws SQLException {
        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT stock_id, company_name, sector, current_price, last_updated, is_active FROM Stock";

        try (Connection conn = dbService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp lastUpdated = rs.getTimestamp("last_updated");
                stocks.add(new Stock(
                        rs.getInt("stock_id"),
                        rs.getString("company_name"),
                        rs.getString("sector"),
                        rs.getDouble("current_price"),
                        lastUpdated != null ? lastUpdated.toInstant() : null,
                        rs.getBoolean("is_active")
                ));
            }
        }
        return stocks;
    }

    public List<Transaction> getClientTransactions(int clientId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE client_id = ? ORDER BY transaction_time DESC";

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp transactionTime = rs.getTimestamp("transaction_time");
                    transactions.add(new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getInt("stock_id"),
                            rs.getInt("client_id"),
                            rs.getDouble("price"),
                            rs.getInt("volume"),
                            rs.getString("direction"),
                            rs.getInt("session_id"),
                            rs.getObject("repo_id", Integer.class),
                            transactionTime != null ? transactionTime.toInstant() : null
                    ));
                }
            }
        }
        return transactions;
    }

    public void executeTrade(int clientId, int stockId, String direction, int volume) throws SQLException {
        String sql = """
            INSERT INTO Transaction (stock_id, client_id, price, volume, direction, session_id, transaction_time)
            SELECT ?, ?, s.current_price, ?, ?, 
                   (SELECT session_id FROM TradingSession WHERE status = 'Active' LIMIT 1),
                   CURRENT_TIMESTAMP
            FROM Stock s
            WHERE s.stock_id = ?
            RETURNING transaction_id
            """;

        dbService.withTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, stockId);
                pstmt.setInt(2, clientId);
                pstmt.setInt(3, volume);
                pstmt.setString(4, direction);
                pstmt.setInt(5, stockId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to execute trade");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<PortfolioItem> getClientPortfolio(int clientId) throws SQLException {
        List<PortfolioItem> portfolio = new ArrayList<>();
        String sql = """
            SELECT t.stock_id, s.company_name, 
                   SUM(CASE WHEN t.direction = 'BUY' THEN t.volume ELSE -t.volume END) AS shares_held,
                   AVG(t.price) AS avg_price
            FROM Transaction t
            JOIN Stock s ON t.stock_id = s.stock_id
            WHERE t.client_id = ?
            GROUP BY t.stock_id, s.company_name
            HAVING SUM(CASE WHEN t.direction = 'BUY' THEN t.volume ELSE -t.volume END) > 0
            """;

        try (Connection conn = dbService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    portfolio.add(new PortfolioItem(
                            rs.getInt("stock_id"),
                            rs.getString("company_name"),
                            rs.getInt("shares_held"),
                            rs.getDouble("avg_price")
                    ));
                }
            }
        }
        return portfolio;
    }

    public record PortfolioItem(int stockId, String companyName, int sharesHeld, double avgPrice) {}
}