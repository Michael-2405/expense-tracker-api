package com.michaelespinosa.expensetracker.expenses.presentation.request;

import com.michaelespinosa.expensetracker.expenses.domain.filter.Period;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record ListExpensesRequest(
        Period period,

        @PastOrPresent
        LocalDate startDate,

        @PastOrPresent
        LocalDate endDate,

        UUID categoryId
) {}
