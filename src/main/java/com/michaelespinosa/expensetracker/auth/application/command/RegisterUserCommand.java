package com.michaelespinosa.expensetracker.auth.application.command;

public record RegisterUserCommand(String firstName, String lastName, String email, String password) { }
