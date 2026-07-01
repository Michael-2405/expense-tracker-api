package com.michaelespinosa.expensetracker.expenses.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.BadRequestException;

public class InvalidExpenseFilterException extends BadRequestException {

    public InvalidExpenseFilterException() {
        super("Period and custom date range cannot be used together.");
    }
}
