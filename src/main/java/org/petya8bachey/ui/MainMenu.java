package org.petya8bachey.ui;

import org.petya8bachey.model.*;
import org.petya8bachey.service.*;

public class MainMenu {
    private final LoginUI loginUI;
    private final TradingService tradingService;
    private final UserService userService;
    private final AuthService authService;

    public MainMenu(DatabaseService dbService) {
        this.userService = new UserService(dbService);
        this.authService = new AuthService(dbService);
        this.tradingService = new TradingService(dbService);
        this.loginUI = new LoginUI(userService, authService);
    }

    public void run() {
        try {
            while (true) {
                LoginUI.AuthResult authResult = loginUI.authenticate();
                if (authResult.client() != null && authResult.role() != null) {
                    UserUI userUI = UIFactory.createUI(
                            authResult.client(),
                            authResult.role(),
                            tradingService
                    );
                    userUI.showMenu();
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}