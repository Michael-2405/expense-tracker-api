package com.michaelespinosa.expensetracker.categories.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "color", nullable = false)
    public String color;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @Column(name = "deleted_at")
    public Instant deletedAt;

    protected  CategoryEntity() {}

    public CategoryEntity(UUID id, String name, String color, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
