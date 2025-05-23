package org.petya8bachey.ui;

import org.petya8bachey.model.Client;
import org.petya8bachey.service.TradingService;

import java.util.Scanner;

public class AdminUI implements UserUI {
    private final TradingService tradingService;
    private final Client client;
    private final Scanner scanner;

    public AdminUI(TradingService tradingService, Client client) {
        this.tradingService = tradingService;
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("1. Manage Brokers");
            System.out.println("2. Manage Clients");
            System.out.println("3. View All Transactions");
            System.out.println("4. System Configuration");
            System.out.println("5. Logout");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> manageBrokers();
                    case 2 -> manageClients();
                    case 3 -> viewAllTransactions();
                    case 4 -> systemConfig();
                    case 5 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void manageBrokers() {
        System.out.println("\nBroker management functionality would be here");
    }

    private void manageClients() {
        System.out.println("\nClient management functionality would be here");
    }

    private void viewAllTransactions() {
        System.out.println("\nViewing all transactions...");
        // Реализация просмотра всех транзакций
    }

    private void systemConfig() {
        System.out.println("\nSystem configuration would be here");
    }

    @Override
    public Client getClient() {
        return client;
    }
}
