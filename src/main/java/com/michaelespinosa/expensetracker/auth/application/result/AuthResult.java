package com.michaelespinosa.expensetracker.auth.application.result;

import java.time.Instant;
import java.util.UUID;

public record AuthResult (UUID id, String firstName, String lastName, String email, Instant createdAt) {}
