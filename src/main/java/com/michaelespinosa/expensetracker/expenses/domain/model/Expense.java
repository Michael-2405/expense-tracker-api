package com.michaelespinosa.expensetracker.expenses.domain.model;

import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotEditableException;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Expense {

    private final UUID id;
    private String title;
    private Money cost;
    private LocalDate expenseDate;
    private final UUID userId;
    private UUID categoryId;
    private final Instant createdAt;
    private Instant updatedAt;

    private Expense(UUID id, String title, Money cost, LocalDate expenseDate, UUID userId,
                    UUID categoryId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.cost = cost;
        this.expenseDate = expenseDate;
        this.userId = userId;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Expense create(String title, Money cost, LocalDate expenseDate, UUID userId, UUID categoryId) {
        Instant now = Instant.now();
        return new Expense(UUID.randomUUID(), title, cost, expenseDate, userId, categoryId, now, now);
    }

    public static Expense reconstitute(UUID id, String title, Money cost, LocalDate expenseDate, UUID userId,
                                       UUID categoryId, Instant createdAt, Instant updatedAt) {
        return new Expense(id, title, cost, expenseDate, userId, categoryId, createdAt, updatedAt);
    }

    public boolean isEditable() {
        return Instant.now().isBefore(createdAt.plus(Duration.ofHours(2)));
    }

    public void edit(String title, Money cost, LocalDate expenseDate, UUID categoryId) {
        if(!isEditable()) {
            throw  new ExpenseNotEditableException();
        }

        this.title = title;
        this.cost = cost;
        this.expenseDate = expenseDate;
        this.categoryId = categoryId;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public String title() { return title; }
    public Money cost() { return cost; }
    public LocalDate expenseDate() { return  expenseDate; }
    public UUID userId() { return userId; }
    public UUID categoryId() { return categoryId; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
