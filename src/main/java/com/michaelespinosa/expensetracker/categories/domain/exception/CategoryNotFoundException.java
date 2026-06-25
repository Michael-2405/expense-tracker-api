package com.michaelespinosa.expensetracker.categories.domain.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException() {
        super("Category not found. Please try again or try another.");
    }
}
