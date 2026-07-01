package com.michaelespinosa.expensetracker.expenses.domain.repository;

import com.michaelespinosa.expensetracker.expenses.domain.filter.ExpenseFilter;
import com.michaelespinosa.expensetracker.expenses.domain.projection.ExpenseSummaryEntry;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {

    void save(Expense expense);

    Optional<Expense> findByIdAndUserId(UUID id, UUID userId);

    List<Expense> findByUserIdAndFilters(UUID userId, ExpenseFilter filter);

    List<ExpenseSummaryEntry> summarizeByMonth(UUID userId, int year, int month);

    void deleteAllByUserId(UUID userId);
}
