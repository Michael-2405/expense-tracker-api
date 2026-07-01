package com.michaelespinosa.expensetracker.expenses.application.command;

import java.util.UUID;

public record GetExpenseDetailCommand (
        UUID id,
        UUID userId
) {}
