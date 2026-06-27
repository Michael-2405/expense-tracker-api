package com.michaelespinosa.expensetracker.expenses.presentation.request;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseRequest(
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
