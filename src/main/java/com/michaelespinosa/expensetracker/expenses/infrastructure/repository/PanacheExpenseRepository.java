package com.michaelespinosa.expensetracker.expenses.infrastructure.repository;

import com.michaelespinosa.expensetracker.expenses.domain.filter.ExpenseFilter;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.projection.ExpenseSummaryEntry;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PanacheExpenseRepository implements ExpenseRepository {

    @Override
    public void save(Expense expense) {
        ExpenseEntity entity = toEntity(expense);
        ExpenseEntity.getEntityManager().merge(entity);
    }

    @Override
    public Optional<Expense> findByIdAndUserId(UUID id, UUID userId) {
        ExpenseEntity entity = ExpenseEntity.find(
                "id = ?1 and userId = ?2", id, userId
        ).firstResult();

        if(entity == null) {
            return Optional.empty();
        }

        return Optional.of(toDomain(entity));
    }

    @Override
    public List<Expense> findByUserIdAndFilters(UUID userId, ExpenseFilter filter) {
        StringBuilder query = new StringBuilder("userId = ?1");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (filter.categoryId() != null) {
            query.append(" and categoryId = ?").append(params.size() + 1);
            params.add(filter.categoryId());
        }

        if (filter.startDate() != null) {
            query.append(" and expenseDate >= ?").append(params.size() + 1);
            params.add(filter.startDate());
        }

        if (filter.endDate() != null) {
            query.append(" and expenseDate <= ?").append(params.size() + 1);
            params.add(filter.endDate());
        }

        List<ExpenseEntity> entities = ExpenseEntity.find(
                query.toString(),
                Sort.descending("expenseDate"),
                params.toArray()
        ).list();

        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<ExpenseSummaryEntry> summarizeByMonth(UUID userId, int year, int month) {
        return ExpenseEntity.getEntityManager()
                .createQuery("""
                SELECT new com.michaelespinosa.expensetracker.expenses.domain.projection.ExpenseSummaryEntry(
                    e.categoryId,
                    e.currency,
                    SUM(e.cost)
                )
                FROM ExpenseEntity e
                WHERE e.userId = :userId
                  AND EXTRACT(YEAR FROM e.expenseDate) = :year
                  AND EXTRACT(MONTH FROM e.expenseDate) = :month
                GROUP BY
                    e.categoryId,
                    e.currency
            """, ExpenseSummaryEntry.class)
                .setParameter("userId", userId)
                .setParameter("year", year)
                .setParameter("month", month)
                .getResultList();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        ExpenseEntity.delete("userId", userId);
    }

    private ExpenseEntity toEntity(Expense expense) {
        return new ExpenseEntity(
                expense.id(),
                expense.title(),
                expense.cost().amount(),
                expense.cost().currency().name(),
                expense.expenseDate(),
                expense.userId(),
                expense.categoryId(),
                expense.createdAt(),
                expense.updatedAt()
        );
    }

    private Expense toDomain(ExpenseEntity entity) {
        return Expense.reconstitute(
                entity.id,
                entity.title,
                Money.of(entity.cost, Currency.valueOf(entity.currency)),
                entity.expenseDate,
                entity.userId,
                entity.categoryId,
                entity.createdAt,
                entity.updatedAt
        );
    }
}
