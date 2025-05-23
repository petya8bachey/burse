package org.petya8bachey.model;

import java.time.Instant;

public record Broker(
        int brokerId,
        String licenseNumber,
        String companyName,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}