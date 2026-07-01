package com.michaelespinosa.expensetracker.shared.domain.exception;

public class BadRequestException extends DomainException {

    protected BadRequestException(String message) {
        super(message);
    }
}
