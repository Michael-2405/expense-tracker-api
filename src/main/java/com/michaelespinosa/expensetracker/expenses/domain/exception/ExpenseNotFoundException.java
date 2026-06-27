package com.michaelespinosa.expensetracker.expenses.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.NotFoundException;

public class ExpenseNotFoundException extends NotFoundException {
    public ExpenseNotFoundException() {
        super("Expense not found. Please try again or try another.");
    }
}
