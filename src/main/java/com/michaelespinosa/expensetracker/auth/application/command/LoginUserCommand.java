package com.michaelespinosa.expensetracker.auth.application.command;

public record LoginUserCommand (
        String email,
        String password
) {}
