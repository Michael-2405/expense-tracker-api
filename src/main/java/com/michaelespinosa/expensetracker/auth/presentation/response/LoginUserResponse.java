package com.michaelespinosa.expensetracker.auth.presentation.response;

import java.time.Instant;

public record LoginUserResponse (
        String token,
        Instant expiresAt
) {}
