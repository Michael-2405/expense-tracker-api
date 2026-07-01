package com.michaelespinosa.expensetracker.expenses.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ValidationException;

public class InvalidAmountException extends ValidationException {

    public InvalidAmountException() {
        super("Amount must be greater than zero.");
    }
}
