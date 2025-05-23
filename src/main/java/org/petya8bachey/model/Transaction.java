package org.petya8bachey.model;

import java.time.Instant;

public record Transaction(
        int transactionId,
        int stockId,
        int clientId,
        double price,
        int volume,
        String direction,
        int sessionId,
        Integer repoId,
        Instant transactionTime
) {}