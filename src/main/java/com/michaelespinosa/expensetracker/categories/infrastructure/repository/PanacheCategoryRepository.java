package com.michaelespinosa.expensetracker.categories.infrastructure.repository;

import com.michaelespinosa.expensetracker.categories.domain.model.Category;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.categories.domain.valueobject.Color;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        CategoryEntity entity = CategoryEntity.findById(id);

        if (entity == null) {
            return Optional.empty();
        }

        Category category = toDomain(entity);

        return Optional.of(category);
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
