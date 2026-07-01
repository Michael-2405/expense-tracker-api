package com.michaelespinosa.expensetracker.categories.domain.repository;

import com.michaelespinosa.expensetracker.categories.domain.model.Category;

import java.util.*;

public interface CategoryRepository {
    List<Category> findAllActive();
    Optional<Category> findActiveById(UUID id);
    Map<UUID, Category> findActiveByIds(Set<UUID> ids);
}
