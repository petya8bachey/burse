package org.petya8bachey.model;

import java.time.Instant;

public record Stock(
        int stockId,
        String companyName,
        String sector,
        double currentPrice,
        Instant lastUpdated,
        boolean isActive
) {}