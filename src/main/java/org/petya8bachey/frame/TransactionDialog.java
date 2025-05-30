package org.petya8bachey.frame;

import org.petya8bachey.enums.TransactionDirection;
import org.petya8bachey.model.Stock;
import org.petya8bachey.model.Client; // Import Client
import org.petya8bachey.model.Session; // Import Session
import org.petya8bachey.model.Repository; // Import Repository
import org.petya8bachey.service.TransactionService;
import org.petya8bachey.service.SessionService; // Import SessionService
import org.petya8bachey.service.RepositoryService; // Import RepositoryService

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // Import BigDecimal
import java.util.List;
import java.util.Optional;
import java.util.Vector; // Use Vector for JComboBox model

public class TransactionDialog extends JDialog {

    private JComboBox<String> stockComboBox;
    private JTextField quantityField;
    private JButton actionButton;
    private JButton cancelButton;
    private JLabel messageLabel;

    private final Client currentClient;
    private final List<Stock> availableStocks;
    private final TransactionDirection direction;
    private final TransactionService transactionService;
    private final SessionService sessionService; // Inject SessionService
    private final RepositoryService repositoryService; // Inject RepositoryService

    private boolean transactionSuccessful = false; // Flag to indicate success

    public TransactionDialog(Frame owner, Client client, List<Stock> stocks, TransactionDirection direction,
                             TransactionService transactionService, SessionService sessionService, RepositoryService repositoryService) {
        super(owner, (direction == TransactionDirection.BUY ? "Купить Акцию" : "Продать Акцию"), true); // Modal dialog

        this.currentClient = client;
        this.availableStocks = stocks;
        this.direction = direction;
        this.transactionService = transactionService;
        this.sessionService = sessionService; // Assign injected service
        this.repositoryService = repositoryService; // Assign injected service

        setSize(300, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Stock Selection
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Акция:"), gbc);
        stockComboBox = new JComboBox<>(new Vector<>(availableStocks.stream()
                .map(stock -> stock.getCompanyName() + " (" + stock.getStockId() + ")")
                .toList()));
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; add(stockComboBox, gbc);

        // Quantity Input
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; add(new JLabel("Количество:"), gbc);
        quantityField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; add(quantityField, gbc);

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; add(messageLabel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionButton = new JButton(direction == TransactionDirection.BUY ? "Купить" : "Продать");
        cancelButton = new JButton("Отмена");

        buttonPanel.add(actionButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; add(buttonPanel, gbc);

        // Add Action Listeners
        actionButton.addActionListener(e -> performTransaction());
        cancelButton.addActionListener(e -> dispose()); // Close the dialog

        // Prevent closing on X button by default, handle it explicitly if needed
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void performTransaction() {
        messageLabel.setText(""); // Clear previous messages

        int selectedIndex = stockComboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            messageLabel.setText("Выберите акцию.");
            return;
        }

        Stock selectedStock = availableStocks.get(selectedIndex);
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) {
                messageLabel.setText("Количество должно быть положительным числом.");
                return;
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Неверный формат количества.");
            return;
        }

        // Find active session
        Optional<Session> activeSessionOpt = sessionService.findActiveSession();
        if (activeSessionOpt.isEmpty()) {
            messageLabel.setText("Нет активной торговой сессии.");
            return;
        }
        Session activeSession = activeSessionOpt.get();

        // Find main repository
        Optional<Repository> mainRepositoryOpt = repositoryService.findMainRepository();
        if (mainRepositoryOpt.isEmpty()) {
            messageLabel.setText("Не найден репозиторий для сделок.");
            return;
        }
        Repository mainRepository = mainRepositoryOpt.get();

        // Get current price from the selected stock
        BigDecimal transactionPrice = selectedStock.getCurrentPrice();

        try {
            // Create and save the transaction
            transactionService.createTransaction(
                    currentClient,
                    selectedStock,
                    activeSession,
                    mainRepository,
                    transactionPrice,
                    quantity,
                    direction
            );

            messageLabel.setForeground(Color.BLUE);
            messageLabel.setText("Сделка успешно совершена!");
            transactionSuccessful = true; // Set success flag

            // Optionally, close the dialog after a short delay or on OK button click
            // For now, we'll just leave the success message and close on dispose().
            // A better approach might be to show a confirmation and then dispose.
            JOptionPane.showMessageDialog(this, "Сделка успешно совершена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close dialog after success message

        } catch (Exception ex) {
            ex.printStackTrace(); // Log the error
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Ошибка при совершении сделки.");
            // You might want more specific error handling here
        }
    }

    // Method to check if the transaction was successful after the dialog is closed
    public boolean isTransactionSuccessful() {
        return transactionSuccessful;
    }
}
