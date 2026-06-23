package com.michaelespinosa.expensetracker.auth.presentation.response;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Instant createdAt,
        String message
) {}
