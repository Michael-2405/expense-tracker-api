package com.michaelespinosa.expensetracker.shared.domain.security;

import java.time.Instant;

public record GeneratedToken(String token, Instant expiresAt) {}
