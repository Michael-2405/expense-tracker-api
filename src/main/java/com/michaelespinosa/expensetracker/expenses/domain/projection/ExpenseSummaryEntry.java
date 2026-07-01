package com.michaelespinosa.expensetracker.expenses.domain.projection;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpenseSummaryEntry(
        UUID categoryId,
        Currency currency,
        BigDecimal totalAmount
) {}
