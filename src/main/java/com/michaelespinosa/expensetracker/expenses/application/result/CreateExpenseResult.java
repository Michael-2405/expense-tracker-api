package com.michaelespinosa.expensetracker.expenses.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import java.util.UUID;

public record CreateExpenseResult (
        String title,
        BigDecimal amount,
        Currency currency,
        LocalDate expenseDate,
        UUID categoryId,
        Instant createdAt
) {}
