package com.michaelespinosa.expensetracker.expenses.application.command;

import java.util.UUID;

public record GetMonthlySummaryCommand(
        UUID userId,
        Integer year,
        Integer month
) {}
