package org.petya8bachey.model;

import java.time.Instant;

public record TradingSession(
        int sessionId,
        Instant startTime,
        Instant endTime,
        String status
) {}
