package com.michaelespinosa.expensetracker.expenses.application.result;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record GetMonthlySummaryResult(
        UUID categoryId,
        String categoryName,
        BigDecimal totalAmount,
        Currency currency
) {}
