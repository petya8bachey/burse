package org.petya8bachey.ui;

import org.petya8bachey.model.Client;
import org.petya8bachey.model.Stock;
import org.petya8bachey.model.Transaction;
import org.petya8bachey.service.TradingService;

import java.util.List;
import java.util.Scanner;

public class BrokerUI implements UserUI {
    private final TradingService tradingService;
    private final Client brokerClient;
    private final Scanner scanner;

    public BrokerUI(TradingService tradingService, Client brokerClient) {
        this.tradingService = tradingService;
        this.brokerClient = brokerClient;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== Broker Dashboard ===");
            System.out.println("1. View My Clients");
            System.out.println("2. View Client Portfolio");
            System.out.println("3. View Client Transactions");
            System.out.println("4. Execute Trade for Client");
            System.out.println("5. View Available Stocks");
            System.out.println("6. Logout");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> viewMyClients();
                    case 2 -> viewClientPortfolio();
                    case 3 -> viewClientTransactions();
                    case 4 -> executeTradeForClient();
                    case 5 -> viewAvailableStocks();
                    case 6 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewMyClients() throws Exception {
        System.out.println("\n=== My Clients ===");
        // В реальном приложении здесь будет запрос к БД для получения клиентов брокера
        System.out.println("Client list functionality would be implemented here");
    }

    private void viewClientPortfolio() throws Exception {
        System.out.print("\nEnter Client ID: ");
        int clientId = scanner.nextInt();
        scanner.nextLine();

        // В реальном приложении нужно проверить, что клиент принадлежит текущему брокеру
        List<TradingService.PortfolioItem> portfolio = tradingService.getClientPortfolio(clientId);

        if (portfolio.isEmpty()) {
            System.out.println("This client doesn't have any stocks in their portfolio.");
            return;
        }

        System.out.println("\n=== Client Portfolio ===");
        System.out.printf("%-5s %-30s %-10s %-10s%n", "ID", "Company", "Shares", "Avg Price");
        portfolio.forEach(item ->
                System.out.printf("%-5d %-30s %-10d $%-10.2f%n",
                        item.stockId(), item.companyName(), item.sharesHeld(), item.avgPrice()));
    }

    private void viewClientTransactions() throws Exception {
        System.out.print("\nEnter Client ID: ");
        int clientId = scanner.nextInt();
        scanner.nextLine();

        List<Transaction> transactions = tradingService.getClientTransactions(clientId);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this client.");
            return;
        }

        System.out.println("\n=== Client Transactions ===");
        System.out.printf("%-10s %-30s %-5s %-10s %-10s %-15s%n",
                "Date", "Company", "Type", "Price", "Shares", "Total");

        for (Transaction t : transactions) {
            String companyName = "Stock#" + t.stockId(); // В реальном приложении нужно получать название акции
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

    private void executeTradeForClient() throws Exception {
        System.out.print("\nEnter Client ID: ");
        int clientId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n=== Available Stocks ===");
        List<Stock> stocks = tradingService.getAllStocks();
        System.out.printf("%-5s %-30s %-15s %-10s%n", "ID", "Company", "Sector", "Price");
        stocks.forEach(stock ->
                System.out.printf("%-5d %-30s %-15s $%-10.2f%n",
                        stock.stockId(), stock.companyName(), stock.sector(), stock.currentPrice()));

        System.out.print("\nEnter Stock ID: ");
        int stockId = scanner.nextInt();
        System.out.print("Enter BUY or SELL: ");
        String direction = scanner.next().toUpperCase();
        System.out.print("Enter number of shares: ");
        int volume = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        tradingService.executeTrade(clientId, stockId, direction, volume);
        System.out.println("Trade executed successfully for client " + clientId);
    }

    private void viewAvailableStocks() throws Exception {
        List<Stock> stocks = tradingService.getAllStocks();
        System.out.println("\n=== Available Stocks ===");
        System.out.printf("%-5s %-30s %-15s %-10s%n", "ID", "Company", "Sector", "Price");
        stocks.forEach(stock ->
                System.out.printf("%-5d %-30s %-15s $%-10.2f%n",
                        stock.stockId(), stock.companyName(), stock.sector(), stock.currentPrice()));
    }

    @Override
    public Client getClient() {
        return brokerClient;
    }
}