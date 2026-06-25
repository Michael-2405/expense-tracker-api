package com.michaelespinosa.expensetracker.categories.domain.model;

import com.michaelespinosa.expensetracker.categories.domain.valueobject.Color;

import java.time.Instant;
import java.util.UUID;

public class Category {

    private final UUID id;
    private final String name;
    private final Color color;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Category(UUID id, String name, Color color,
                     Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static Category reconstitute(UUID id, String name, Color color,
                                        Instant createdAt, Instant updatedAt, Instant deletedAt) {
        return new Category(id, name, color, createdAt, updatedAt, deletedAt);
    }

    public boolean isActive() {
        return deletedAt == null;
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public Color color() { return color; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant deletedAt() { return deletedAt; }
}
