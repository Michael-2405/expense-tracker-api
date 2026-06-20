package com.michaelespinosa.expensetracker.auth.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ValidationException;

public class InvalidPasswordException extends ValidationException{

    public InvalidPasswordException(String message) {
        super(message);
    }
}
