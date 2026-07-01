package com.michaelespinosa.expensetracker.expenses.domain.filter;

import java.time.LocalDate;
import java.util.UUID;

public record ExpenseFilter(
        LocalDate startDate,
        LocalDate endDate,
        UUID categoryId
) {}
