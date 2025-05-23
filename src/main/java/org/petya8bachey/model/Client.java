package org.petya8bachey.model;

import java.time.Instant;
import java.time.LocalDate;

public record Client(
        int clientId,
        String fullName,
        String taxId,
        String clientType,
        LocalDate registrationDate,
        int brokerId,
        String dbUsername,  // Добавлено
        String dbPassword,  // Добавлено
        Instant createdAt,
        Instant updatedAt
) {}