package org.petya8bachey.ui;

import org.petya8bachey.model.*;
import org.petya8bachey.service.TradingService;

import java.util.List;
import java.util.Scanner;

public class ClientUI implements UserUI{
    private final TradingService tradingService;
    private final Scanner scanner;
    private final Client client;

    public ClientUI(TradingService tradingService, Client client) {
        this.tradingService = tradingService;
        this.scanner = new Scanner(System.in);
        this.client = client;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Trading System ===");
            System.out.println("1. View Stocks");
            System.out.println("2. View Portfolio");
            System.out.println("3. View Transactions");
            System.out.println("4. Buy Stock");
            System.out.println("5. Sell Stock");
            System.out.println("6. Logout");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> showStocks();
                    case 2 -> showPortfolio();
                    case 3 -> showTransactions();
                    case 4 -> showBuyStock();
                    case 5 -> showSellStock();
                    case 6 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public Client getClient() {
        return client;
    }

    private void showStocks() throws Exception {
        List<Stock> stocks = tradingService.getAllStocks();
        System.out.println("\n=== Available Stocks ===");
        System.out.printf("%-5s %-30s %-15s %-10s%n", "ID", "Company", "Sector", "Price");
        stocks.forEach(stock ->
                System.out.printf("%-5d %-30s %-15s $%-10.2f%n",
                        stock.stockId(), stock.companyName(), stock.sector(), stock.currentPrice()));
    }

    private void showPortfolio() throws Exception {
        List<TradingService.PortfolioItem> portfolio = tradingService.getClientPortfolio(client.clientId());
        if (portfolio.isEmpty()) {
            System.out.println("\nYou don't have any stocks in your portfolio.");
            return;
        }

        System.out.println("\n=== Your Portfolio ===");
        System.out.printf("%-5s %-30s %-10s %-10s%n", "ID", "Company", "Shares", "Avg Price");
        portfolio.forEach(item ->
                System.out.printf("%-5d %-30s %-10d $%-10.2f%n",
                        item.stockId(), item.companyName(), item.sharesHeld(), item.avgPrice()));
    }

    private void showTransactions() throws Exception {
        List<Transaction> transactions = tradingService.getClientTransactions(client.clientId());
        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions found.");
            return;
        }

        System.out.println("\n=== Your Transactions ===");
        System.out.printf("%-10s %-30s %-5s %-10s %-10s %-15s%n",
                "Date", "Company", "Type", "Price", "Shares", "Total");

        for (Transaction t : transactions) {
            // In a real app, we'd fetch company name from stock_id
            String companyName = "Stock#" + t.stockId();
            double total = t.price() * t.volume();
            System.out.printf("%-10s %-30s %-5s $%-10.2f %-10d $%-15.2f%n",
                    t.transactionTime().toString().substring(0, 10),
                    companyName,
                    t.direction(),
                    t.price(),
                    t.volume(),
                    total);
        }
    }

    private void showBuyStock() throws Exception {
        showStocks();
        System.out.print("\nEnter Stock ID to buy: ");
        int stockId = scanner.nextInt();
        System.out.print("Enter number of shares: ");
        int volume = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        tradingService.executeTrade(client.clientId(), stockId, "BUY", volume);
        System.out.println("Buy order executed successfully.");
    }

    private void showSellStock() throws Exception {
        showPortfolio();
        System.out.print("\nEnter Stock ID to sell: ");
        int stockId = scanner.nextInt();
        System.out.print("Enter number of shares: ");
        int volume = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        tradingService.executeTrade(client.clientId(), stockId, "SELL", volume);
        System.out.println("Sell order executed successfully.");
    }
}