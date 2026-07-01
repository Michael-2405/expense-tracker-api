package com.michaelespinosa.expensetracker.expenses.presentation.response;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ListExpensesResponse (
        UUID id,
        String title,
        BigDecimal amount,
        Currency currency,
        LocalDate expenseDate,
        UUID categoryId,
        Instant createdAt,
        Instant updatedAt
) {}
