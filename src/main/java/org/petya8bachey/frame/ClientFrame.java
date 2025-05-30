package org.petya8bachey.frame;

import org.petya8bachey.enums.TransactionDirection;
import org.petya8bachey.model.Client;
import org.petya8bachey.model.Stock;
import org.petya8bachey.model.Transaction;
import org.petya8bachey.service.StockService;
import org.petya8bachey.service.TransactionService;
import org.petya8bachey.service.UserService;
import org.petya8bachey.service.SessionService;
import org.petya8bachey.service.RepositoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class ClientFrame extends JFrame {

    private final UserService userService;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final SessionService sessionService;
    private final RepositoryService repositoryService;
    private final Client currentClient;

    private JTabbedPane tabbedPane;
    private JPanel transactionsPanel;

    public ClientFrame(Client client, UserService userService, StockService stockService,
                       TransactionService transactionService, SessionService sessionService,
                       RepositoryService repositoryService) {
        this.userService = userService;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.sessionService = sessionService;
        this.repositoryService = repositoryService;
        this.currentClient = client;

        if (client == null) {
            JOptionPane.showMessageDialog(null, "Ошибка: Данные клиента не найдены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            return;
        }

        setTitle("Окно клиента: " + client.getFullName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        tabbedPane = new JTabbedPane();

        JPanel personalInfoPanel = createPersonalInfoPanel(client);
        tabbedPane.addTab("Мои Данные", personalInfoPanel);

        JPanel tradedStocksPanel = createTradedStocksPanel(client);
        tabbedPane.addTab("Мои Торгуемые Акции", tradedStocksPanel);

        transactionsPanel = createTransactionsPanel(client);
        tabbedPane.addTab("Мои Сделки", transactionsPanel);

        JPanel availableStocksPanel = createAvailableStocksPanel();
        tabbedPane.addTab("Доступные Акции", availableStocksPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Выход");
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame(userService, stockService, transactionService, sessionService, repositoryService).setVisible(true); // Pass new services
            });
        });
        JPanel logoutButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.add(logoutButton);
        bottomPanel.add(logoutButtonPanel, BorderLayout.EAST);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton buyButton = new JButton("Купить Акцию");
        buyButton.addActionListener(e -> openTransactionDialog(TransactionDirection.BUY));

        JButton sellButton = new JButton("Продать Акцию");
        sellButton.addActionListener(e -> openTransactionDialog(TransactionDirection.SELL));

        actionButtonsPanel.add(buyButton);
        actionButtonsPanel.add(sellButton);
        bottomPanel.add(actionButtonsPanel, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPersonalInfoPanel(Client client) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Полное имя:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(new JLabel(client.getFullName()), gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("ИНН:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(new JLabel(client.getTaxId()), gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Тип клиента:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(new JLabel(client.getClientType().toString()), gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Дата регистрации:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panel.add(new JLabel(client.getRegistrationDate().toString()), gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Брокер:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; panel.add(new JLabel(client.getBroker() != null ? client.getBroker().getCompanyName() : "Не привязан"), gbc);

        return panel;
    }

    private JPanel createTradedStocksPanel(Client client) {
        JPanel panel = new JPanel(new BorderLayout());
        Set<Stock> tradedStocks = client.getTradedStocks() != null ? client.getTradedStocks() : new java.util.HashSet<>();

        if (tradedStocks.isEmpty()) {
            panel.add(new JLabel("Вы пока не торговали никакими акциями.", SwingConstants.CENTER), BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"Тикер", "Название компании", "Сектор", "Текущая цена"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Stock stock : tradedStocks) {
            model.addRow(new Object[]{
                    stock.getStockId(),
                    stock.getCompanyName(),
                    stock.getSector(),
                    stock.getCurrentPrice()
            });
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTransactionsPanel(Client client) {
        JPanel panel = new JPanel(new BorderLayout());
        List<Transaction> transactions = transactionService.getTransactionsByClient(client);

        if (transactions == null || transactions.isEmpty()) {
            panel.add(new JLabel("У вас пока нет совершенных сделок.", SwingConstants.CENTER), BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"ID Сделки", "Акция", "Направление", "Объем", "Цена", "Дата/Время Сессии"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Transaction tx : transactions) {
            String sessionTime = "N/A";
            if (tx.getSession() != null) {
                try {
                    sessionTime = tx.getSession().getStartTime().toString();
                } catch (org.hibernate.LazyInitializationException lie) {
                    System.err.println("LazyInitializationException caught for Session in Transaction: " + tx.getTransactionId());
                    sessionTime = "Error Loading Session";
                }
            }

            model.addRow(new Object[]{
                    tx.getTransactionId().toString().substring(0, 8) + "...",
                    tx.getStock().getCompanyName() + " (" + tx.getStock().getStockId() + ")",
                    tx.getDirection(),
                    tx.getVolume(),
                    tx.getPrice(),
                    sessionTime
            });
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAvailableStocksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Stock> allStocks = stockService.findAllStocks();

        if (allStocks == null || allStocks.isEmpty()) {
            panel.add(new JLabel("В системе пока нет зарегистрированных акций.", SwingConstants.CENTER), BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"Тикер", "Название компании", "Сектор", "Текущая цена"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Stock stock : allStocks) {
            model.addRow(new Object[]{
                    stock.getStockId(),
                    stock.getCompanyName(),
                    stock.getSector(),
                    stock.getCurrentPrice()
            });
        }

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // New method to open the transaction dialog
    private void openTransactionDialog(TransactionDirection direction) {
        List<Stock> availableStocks = stockService.findAllStocks();

        if (availableStocks == null || availableStocks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет доступных акций для торговли.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TransactionDialog dialog = new TransactionDialog(
                this,
                currentClient,
                availableStocks,
                direction,
                transactionService,
                sessionService,
                repositoryService
        );
        dialog.setVisible(true);

        if (dialog.isTransactionSuccessful()) {
            refreshTransactionsPanel();
        }
    }

    private void refreshTransactionsPanel() {
        int transactionsTabIndex = -1;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if ("Мои Сделки".equals(tabbedPane.getTitleAt(i))) {
                transactionsTabIndex = i;
                break;
            }
        }

        if (transactionsTabIndex != -1) {
            tabbedPane.removeTabAt(transactionsTabIndex);
            transactionsPanel = createTransactionsPanel(currentClient);
            tabbedPane.insertTab("Мои Сделки", null, transactionsPanel, null, transactionsTabIndex);
            tabbedPane.setSelectedIndex(transactionsTabIndex);
        }
    }
}
