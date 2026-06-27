package com.michaelespinosa.expensetracker.expenses.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ValidationException;

public class InvalidExpenseDateException extends ValidationException {
    public InvalidExpenseDateException() {
        super("Expense date cannot be in the future");
    }
}
