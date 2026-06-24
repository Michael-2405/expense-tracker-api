package com.michaelespinosa.expensetracker.expenses.domain.repository;

import java.util.UUID;

public interface ExpenseRepository {
    void deleteAllByUserId(UUID userId);
}
