package com.michaelespinosa.expensetracker.expenses.presentation.request;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EditExpenseRequest(
        @NotBlank
        String title,

        @NotNull
        @Positive
        BigDecimal amount,

        @NotNull
        Currency currency,

        @NotNull
        @PastOrPresent
        LocalDate expenseDate,

        @NotNull
        UUID categoryId
) {}
