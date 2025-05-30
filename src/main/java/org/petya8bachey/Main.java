package org.petya8bachey;

import org.petya8bachey.frame.LoginFrame;
import org.petya8bachey.service.StockService;
import org.petya8bachey.service.TransactionService;
import org.petya8bachey.service.UserService;
import org.petya8bachey.service.SessionService;
import org.petya8bachey.service.RepositoryService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;

@SpringBootApplication
@ComponentScan(basePackages = "org.petya8bachey")
public class Main {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setHeadless(false);
        ApplicationContext context = app.run(args);

        UserService userService = context.getBean(UserService.class);
        StockService stockService = context.getBean(StockService.class);
        TransactionService transactionService = context.getBean(TransactionService.class);
        SessionService sessionService = context.getBean(SessionService.class);
        RepositoryService repositoryService = context.getBean(RepositoryService.class);

        SwingUtilities.invokeLater(() -> {
            new LoginFrame(userService, stockService, transactionService, sessionService, repositoryService).setVisible(true); // Pass new services
        });
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
