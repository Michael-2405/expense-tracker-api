package com.michaelespinosa.expensetracker.expenses.presentation.response;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record GetMonthlySummaryResponse(
        UUID categoryId,
        String categoryName,
        Currency currency,
        BigDecimal totalAmount
) {}
