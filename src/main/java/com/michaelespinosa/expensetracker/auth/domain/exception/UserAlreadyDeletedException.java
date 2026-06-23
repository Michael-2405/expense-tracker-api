package com.michaelespinosa.expensetracker.auth.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ConflictException;

public class UserAlreadyDeletedException extends ConflictException{

    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}
