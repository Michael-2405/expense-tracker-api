package com.michaelespinosa.expensetracker.categories.application.result;

import java.time.Instant;
import java.util.UUID;

public record CategoryResult (
        UUID id,
        String name,
        String color,
        Instant createdAt
) {}
