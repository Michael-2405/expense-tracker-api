package com.michaelespinosa.expensetracker.expenses.application.command;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EditExpenseCommand(
        UUID id,
        String title,
        BigDecimal amount,
        Currency currency,
        LocalDate expenseDate,
        UUID userId,
        UUID categoryId
) {}
