package org.petya8bachey.ui;

import org.petya8bachey.model.Client;
import org.petya8bachey.model.Stock;
import org.petya8bachey.service.TradingService;

import java.util.List;
import java.util.Scanner;

public class AnalystUI implements UserUI {
    private final TradingService tradingService;
    private final Client analystClient;
    private final Scanner scanner;

    public AnalystUI(TradingService tradingService, Client analystClient) {
        this.tradingService = tradingService;
        this.analystClient = analystClient;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== Analyst Dashboard ===");
            System.out.println("1. View All Stocks Analysis");
            System.out.println("2. View Market Trends");
            System.out.println("3. View Transaction History");
            System.out.println("4. Generate Sector Report");
            System.out.println("5. View Client Statistics");
            System.out.println("6. Logout");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> viewAllStocksAnalysis();
                    case 2 -> viewMarketTrends();
                    case 3 -> viewTransactionHistory();
                    case 4 -> generateSectorReport();
                    case 5 -> viewClientStatistics();
                    case 6 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewAllStocksAnalysis() throws Exception {
        List<Stock> stocks = tradingService.getAllStocks();
        System.out.println("\n=== Stocks Analysis ===");
        System.out.printf("%-5s %-30s %-15s %-10s %-15s%n",
                "ID", "Company", "Sector", "Price", "Status");

        stocks.forEach(stock ->
                System.out.printf("%-5d %-30s %-15s $%-10.2f %-15s%n",
                        stock.stockId(),
                        stock.companyName(),
                        stock.sector(),
                        stock.currentPrice(),
                        stock.isActive() ? "Active" : "Inactive"));
    }

    private void viewMarketTrends() {
        System.out.println("\n=== Market Trends ===");
        System.out.println("This would show various market trends and analytics");
        // Реализация анализа рыночных тенденций
    }

    private void viewTransactionHistory() throws Exception {
        System.out.println("\n=== Transaction History ===");
        // В реальном приложении здесь будет сложный запрос для аналитики
        System.out.println("Full transaction history analysis would be here");
    }

    private void generateSectorReport() throws Exception {
        List<Stock> stocks = tradingService.getAllStocks();
        System.out.println("\n=== Sector Performance Report ===");

        stocks.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Stock::sector,
                        java.util.stream.Collectors.averagingDouble(Stock::currentPrice)))
                .forEach((sector, avgPrice) ->
                        System.out.printf("%-15s: Average Price $%.2f%n", sector, avgPrice));
    }

    private void viewClientStatistics() {
        System.out.println("\n=== Client Statistics ===");
        System.out.println("Detailed client statistics would be shown here");
        // Реализация статистики по клиентам
    }

    @Override
    public Client getClient() {
        return analystClient;
    }
}