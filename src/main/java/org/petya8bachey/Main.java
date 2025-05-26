package org.petya8bachey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // NEW
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // NEW
import org.springframework.security.crypto.password.PasswordEncoder; // NEW

@SpringBootApplication
@ComponentScan(basePackages = "org.petya8bachey") // Убедитесь, что Spring сканирует ваши пакеты

public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // NEW: Define a PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
