package com.michaelespinosa.expensetracker.auth.application.command;

import java.util.UUID;

public record DeleteAccountCommand (UUID userId, String email, String password) {}
