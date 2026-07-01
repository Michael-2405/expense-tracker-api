package com.michaelespinosa.expensetracker.auth.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ConflictException;

public class UserAlreadyExistsException extends ConflictException {

    public UserAlreadyExistsException(String email) {
        super("User already exists with email: " + email);
    }
}
