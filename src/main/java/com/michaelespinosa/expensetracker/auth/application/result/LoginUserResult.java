package com.michaelespinosa.expensetracker.auth.application.result;

import java.time.Instant;

public record LoginUserResult (
        String token,
        Instant expiresAt
) {}
