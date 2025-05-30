package org.petya8bachey.frame;

import org.petya8bachey.model.Broker;
import org.petya8bachey.service.StockService;
import org.petya8bachey.service.TransactionService;
import org.petya8bachey.service.UserService;
import org.petya8bachey.service.SessionService; // NEW
import org.petya8bachey.service.RepositoryService; // NEW
import javax.swing.*;
import java.awt.*;

public class BrokerFrame extends JFrame {

    private JLabel companyNameLabel;
    private JLabel licenseNumberLabel;
    private JLabel statusLabel;

    private final UserService userService;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final SessionService sessionService;
    private final RepositoryService repositoryService;

    public BrokerFrame(Broker broker, UserService userService, StockService stockService,
                       TransactionService transactionService, SessionService sessionService,
                       RepositoryService repositoryService) {
        this.userService = userService;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.sessionService = sessionService;
        this.repositoryService = repositoryService;

        if (broker == null) {
            JOptionPane.showMessageDialog(null, "Ошибка: Данные брокера не найдены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            return;
        }

        setTitle("Окно брокера: " + broker.getCompanyName());
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; infoPanel.add(new JLabel("Название компании:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        companyNameLabel = new JLabel(broker.getCompanyName());
        infoPanel.add(companyNameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; infoPanel.add(new JLabel("Номер лицензии:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        licenseNumberLabel = new JLabel(broker.getLicenseNumber());
        infoPanel.add(licenseNumberLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; infoPanel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        statusLabel = new JLabel(broker.getStatus().toString());
        infoPanel.add(statusLabel, gbc);

        add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(new JButton("Управление клиентами"));
        buttonPanel.add(new JButton("Управление акциями"));

        JButton logoutButton = new JButton("Выход");
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame(userService, stockService, transactionService, sessionService, repositoryService).setVisible(true);
            });
        });
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
