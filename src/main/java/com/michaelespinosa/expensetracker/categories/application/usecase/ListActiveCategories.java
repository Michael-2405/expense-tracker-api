package com.michaelespinosa.expensetracker.categories.application.usecase;

import com.michaelespinosa.expensetracker.categories.application.result.CategoryResult;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListActiveCategories {

    private final CategoryRepository categoryRepository;

    public ListActiveCategories(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResult> execute() {
        return categoryRepository.findAllActive()
                .stream()
                .map(category -> new CategoryResult(
                        category.id(),
                        category.name(),
                        category.color().value(),
                        category.createdAt()
                ))
                .toList();
    }
}
