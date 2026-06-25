package com.michaelespinosa.expensetracker.categories.domain.repository;

import com.michaelespinosa.expensetracker.categories.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    List<Category> findAllActive();
    Optional<Category> findActiveById(UUID id);
}
