package com.michaelespinosa.expensetracker.expenses.application.command;

import com.michaelespinosa.expensetracker.expenses.domain.filter.Period;

import java.time.LocalDate;
import java.util.UUID;

public record ListExpensesCommand(
        UUID userId,
        Period period,
        LocalDate startDate,
        LocalDate endDate,
        UUID categoryId
) {}
