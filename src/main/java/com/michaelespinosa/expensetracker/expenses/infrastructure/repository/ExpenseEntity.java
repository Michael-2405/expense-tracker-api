package com.michaelespinosa.expensetracker.expenses.infrastructure.repository;

import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class ExpenseEntity extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "cost", nullable = false)
    public BigDecimal cost;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    public Currency currency;

    @Column(name = "expense_date", nullable = false)
    public LocalDate expenseDate;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "category_id", nullable = false)
    public UUID categoryId;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    protected ExpenseEntity() {}

    public ExpenseEntity(UUID id, String title, BigDecimal cost, Currency currency, LocalDate expenseDate,
                         UUID userId, UUID categoryId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.cost = cost;
        this.currency = currency;
        this.expenseDate = expenseDate;
        this.userId = userId;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
