package com.michaelespinosa.expensetracker.shared.domain.exception;

public abstract class ValidationException extends  DomainException {

    protected ValidationException(String message) {
        super(message);
    }
}
