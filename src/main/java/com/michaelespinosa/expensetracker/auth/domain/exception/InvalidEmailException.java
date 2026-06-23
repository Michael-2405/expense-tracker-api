package com.michaelespinosa.expensetracker.auth.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ValidationException;

public class InvalidEmailException extends ValidationException{

    public InvalidEmailException(String email) {
        super("Invalid email format: " + email);
    }
}
