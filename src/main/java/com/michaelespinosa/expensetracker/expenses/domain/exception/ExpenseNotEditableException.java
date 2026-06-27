package com.michaelespinosa.expensetracker.expenses.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ConflictException;

public class ExpenseNotEditableException extends ConflictException {
    public ExpenseNotEditableException() {
        super("Expense cannot be editable, time for edit has ran out.");
    }
}
