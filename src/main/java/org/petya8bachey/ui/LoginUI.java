package org.petya8bachey.ui;

import org.petya8bachey.model.Client;
import org.petya8bachey.service.AuthService;
import org.petya8bachey.service.UserService;

import java.util.Scanner;
import java.util.Optional;

public class LoginUI {
    private final UserService userService;
    private final AuthService authService;
    private final Scanner scanner;

    public LoginUI(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.scanner = new Scanner(System.in);
    }

    public AuthResult authenticate() {
        System.out.println("\n=== Trading System Login ===");
        System.out.print("Enter your database username: ");
        String dbUsername = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine(); // В реальном приложении используйте более безопасный метод

        try {
            // Здесь должна быть реальная аутентификация через JDBC
            // Для примера просто проверяем роль

            String role = authService.determineUserRole(dbUsername);
            if (role == null) {
                System.out.println("User has no valid roles assigned");
                return new AuthResult(null, null);
            }

            System.out.print("Enter your Tax ID: ");
            String taxId = scanner.nextLine();

            Optional<Client> client = userService.authenticateClient(taxId);
            if (client.isPresent()) {
                userService.updateCurrentClient(client.get().clientId());
                System.out.printf("Login successful. Welcome, %s! Role: %s%n",
                        client.get().fullName(), role);
                return new AuthResult(client.get(), role);
            } else {
                System.out.println("Invalid Tax ID. Please try again.");
                return new AuthResult(null, null);
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            return new AuthResult(null, null);
        }
    }

    public record AuthResult(Client client, String role) {}
}