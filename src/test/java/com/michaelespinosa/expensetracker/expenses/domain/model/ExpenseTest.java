package com.michaelespinosa.expensetracker.expenses.domain.model;

import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotEditableException;
import com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidExpenseDateException;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {

    private final Money validMoney = Money.of(new BigDecimal("100.00"), Currency.USD);
    private final LocalDate today = LocalDate.now();
    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @Test
    void shouldBeEditableBeforeTwoHours() {
        Expense expense = Expense.create("Netflix", validMoney, today, userId, categoryId);
        assertTrue(expense.isEditable());
    }

    @Test
    void shouldNotBeEditableAfterTwoHours() {
        Expense expense = Expense.reconstitute(
                UUID.randomUUID(), "Netflix", validMoney, today,
                userId, categoryId,
                Instant.now().minus(Duration.ofHours(3)),
                Instant.now().minus(Duration.ofHours(3))
        );
        assertFalse(expense.isEditable());
    }

    @Test
    void shouldEditAllFieldsWhenEditable() {
        Expense expense = Expense.create("Netflix", validMoney, today, userId, categoryId);
        Money newMoney = Money.of(new BigDecimal("200.00"), Currency.EUR);
        UUID newCategoryId = UUID.randomUUID();

        expense.edit("Spotify", newMoney, today, newCategoryId);

        assertEquals("Spotify", expense.title());
        assertEquals(newMoney, expense.cost());
        assertEquals(newCategoryId, expense.categoryId());
    }

    @Test
    void shouldUpdateUpdatedAtWhenEdited() {
        Expense expense = Expense.create("Netflix", validMoney, today, userId, categoryId);
        Instant before = expense.updatedAt();

        expense.edit("Spotify", validMoney, today, categoryId);

        assertTrue(expense.updatedAt().isAfter(before));
    }

    @Test
    void shouldThrowWhenEditingAfterTwoHours() {
        Expense expense = Expense.reconstitute(
                UUID.randomUUID(), "Netflix", validMoney, today,
                userId, categoryId,
                Instant.now().minus(Duration.ofHours(3)),
                Instant.now().minus(Duration.ofHours(3))
        );
        assertThrows(ExpenseNotEditableException.class,
                () -> expense.edit("Spotify", validMoney, today, categoryId));
    }

    @Test
    void shouldThrowWhenCreatingWithFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThrows(InvalidExpenseDateException.class,
                () -> Expense.create("Netflix", validMoney, futureDate, userId, categoryId));
    }

    @Test
    void shouldThrowWhenEditingWithFutureDate() {
        Expense expense = Expense.create("Netflix", validMoney, today, userId, categoryId);
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThrows(InvalidExpenseDateException.class,
                () -> expense.edit("Netflix", validMoney, futureDate, categoryId));
    }
}
