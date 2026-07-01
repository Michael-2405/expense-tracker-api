package com.michaelespinosa.expensetracker.auth.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Invalid credentials, please try again.");
    }
}
