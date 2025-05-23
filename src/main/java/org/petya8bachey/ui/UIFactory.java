package org.petya8bachey.ui;

import org.petya8bachey.model.Client;
import org.petya8bachey.service.TradingService;

public class UIFactory {
    public static UserUI createUI(Client client, String role, TradingService tradingService) {
        return switch (role) {
            case "trading_admin" -> new AdminUI(tradingService, client);
            case "trading_broker" -> new BrokerUI(tradingService, client);
            case "trading_analyst" -> new AnalystUI(tradingService, client);
            case "trading_client" -> new ClientUI(tradingService, client);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}