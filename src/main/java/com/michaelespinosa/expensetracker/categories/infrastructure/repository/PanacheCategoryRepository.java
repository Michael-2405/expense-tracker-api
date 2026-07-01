package com.michaelespinosa.expensetracker.categories.infrastructure.repository;

import com.michaelespinosa.expensetracker.categories.domain.model.Category;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.categories.domain.valueobject.Color;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PanacheCategoryRepository implements CategoryRepository {

    @Override
    public List<Category> findAllActive() {
        return CategoryEntity.find("deletedAt IS NULL")
                .list()
                .stream()
                .map(entity -> toDomain((CategoryEntity) entity))
                .toList();
    }

    @Override
    public Optional<Category> findActiveById(UUID id) {
        CategoryEntity entity = CategoryEntity.find(
                "id = ?1 AND deletedAt IS NULL", id
        ).firstResult();

        if (entity == null) {
            return Optional.empty();
        }

        Category category = toDomain(entity);

        return Optional.of(category);
    }

    @Override
    public Map<UUID, Category> findActiveByIds(Set<UUID> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        return CategoryEntity.find(
                "id IN ?1 AND deletedAt IS NULL", ids
        )
        .list()
        .stream()
        .map(CategoryEntity.class::cast)
        .map(this::toDomain)
        .collect(Collectors.toMap(
                Category::id,
                category -> category
        ));
    }



    private CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.id(),
                category.name(),
                category.color().toString(),
                category.createdAt(),
                category.updatedAt(),
                category.deletedAt()
        );
    }

    private Category toDomain(CategoryEntity entity) {
        return Category.reconstitute(
                entity.id,
                entity.name,
                Color.of(entity.color),
                entity.createdAt,
                entity.updatedAt,
                entity.deletedAt
        );
    }
}
