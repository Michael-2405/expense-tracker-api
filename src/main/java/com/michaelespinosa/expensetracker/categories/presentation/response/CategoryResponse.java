package com.michaelespinosa.expensetracker.categories.presentation.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String color
) {}
