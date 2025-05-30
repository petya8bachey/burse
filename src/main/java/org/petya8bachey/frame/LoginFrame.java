package org.petya8bachey.frame;

import org.petya8bachey.enums.UserRole;
import org.petya8bachey.model.Client;
import org.petya8bachey.model.Broker;
import org.petya8bachey.model.User;
import org.petya8bachey.service.StockService;
import org.petya8bachey.service.TransactionService;
import org.petya8bachey.service.UserService;
import org.petya8bachey.service.SessionService;
import org.petya8bachey.service.RepositoryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private final UserService userService;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final SessionService sessionService;
    private final RepositoryService repositoryService;

    public LoginFrame(UserService userService, StockService stockService,
                      TransactionService transactionService, SessionService sessionService,
                      RepositoryService repositoryService) {
        this.userService = userService;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.sessionService = sessionService;
        this.repositoryService = repositoryService;

        setTitle("Авторизация");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; add(new JLabel("Имя пользователя:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; add(new JLabel("Пароль:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; add(passwordField, gbc);

        loginButton = new JButton("Войти");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; add(loginButton, gbc);

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; add(messageLabel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Optional<User> authenticatedUserOptional = userService.authenticate(username, password);

        if (authenticatedUserOptional.isPresent()) {
            User user = authenticatedUserOptional.get();
            messageLabel.setForeground(Color.BLUE);
            messageLabel.setText("Авторизация успешна!");
            JOptionPane.showMessageDialog(this, "Добро пожаловать, " + user.getUsername() + "!", "Успех", JOptionPane.INFORMATION_MESSAGE);

            if (user.getRole() == UserRole.CLIENT) {
                Client clientProfile = user.getClientProfile();
                if (clientProfile != null) {
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        new ClientFrame(clientProfile, userService, stockService, transactionService, sessionService, repositoryService).setVisible(true);
                    });
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("У пользователя-клиента нет профиля клиента.");
                }
            } else if (user.getRole() == UserRole.BROKER) {
                Broker brokerProfile = user.getBrokerProfile();
                if (brokerProfile != null) {
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        new BrokerFrame(brokerProfile, userService, stockService, transactionService, sessionService, repositoryService).setVisible(true);
                    });
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("У пользователя-брокера нет профиля брокера.");
                }
            } else if (user.getRole() == UserRole.ADMIN) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new AdminFrame(user, userService, stockService, transactionService, sessionService, repositoryService).setVisible(true);
                });
            }

        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Неверное имя пользователя или пароль.");
        }
    }
}
