package org.petya8bachey.frame;

import org.petya8bachey.model.User;
import org.petya8bachey.service.StockService;
import org.petya8bachey.service.TransactionService;
import org.petya8bachey.service.UserService;
import org.petya8bachey.service.SessionService; // NEW
import org.petya8bachey.service.RepositoryService; // NEW
import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    private JLabel usernameLabel;
    private JLabel roleLabel;
    private JLabel welcomeMessageLabel;

    private final UserService userService;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final SessionService sessionService;
    private final RepositoryService repositoryService;

    public AdminFrame(User adminUser, UserService userService, StockService stockService,
                      TransactionService transactionService, SessionService sessionService,
                      RepositoryService repositoryService) {
        this.userService = userService;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.sessionService = sessionService;
        this.repositoryService = repositoryService;

        if (adminUser == null) {
            JOptionPane.showMessageDialog(null, "Ошибка: Данные администратора не найдены.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            return;
        }

        setTitle("Окно администратора: " + adminUser.getUsername());
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; infoPanel.add(new JLabel("Имя пользователя:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        usernameLabel = new JLabel(adminUser.getUsername());
        infoPanel.add(usernameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; infoPanel.add(new JLabel("Роль:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        roleLabel = new JLabel(adminUser.getRole().toString());
        infoPanel.add(roleLabel, gbc);

        add(infoPanel, BorderLayout.NORTH);

        welcomeMessageLabel = new JLabel("<html><center>Добро пожаловать, Администратор!<br>Здесь вы можете управлять всеми пользователями, брокерами, клиентами, акциями и сессиями.</center></html>", SwingConstants.CENTER);
        welcomeMessageLabel.setFont(new Font("Serif", Font.BOLD, 16));
        add(welcomeMessageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(new JButton("Управление пользователями"));
        buttonPanel.add(new JButton("Управление брокерами"));
        buttonPanel.add(new JButton("Управление клиентами"));
        buttonPanel.add(new JButton("Управление акциями"));
        buttonPanel.add(new JButton("Управление сессиями"));

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
