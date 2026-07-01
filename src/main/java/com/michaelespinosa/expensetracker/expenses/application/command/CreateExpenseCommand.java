package com.michaelespinosa.expensetracker.expenses.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;

public record CreateExpenseCommand(
        UUID userId,
        String title,
        BigDecimal amount,
        Currency currency,
        LocalDate expenseDate,
        UUID categoryId
) {}
