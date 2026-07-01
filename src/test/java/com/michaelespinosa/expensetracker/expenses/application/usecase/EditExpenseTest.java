package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.categories.domain.exception.CategoryNotFoundException;
import com.michaelespinosa.expensetracker.categories.domain.model.Category;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.expenses.application.command.EditExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.EditExpenseResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotEditableException;
import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotFoundException;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EditExpenseTest {

    ExpenseRepository expenseRepository;
    CategoryRepository categoryRepository;
    EditExpense editExpense;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        editExpense = new EditExpense(expenseRepository, categoryRepository);
    }

    @Test
    void shouldEditExpenseSuccessfully() {
        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.create(
                "Netflix",
                Money.of(new BigDecimal("10.00"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId
        );

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(expense));

        when(categoryRepository.findActiveById(categoryId))
                .thenReturn(Optional.of(mock(Category.class)));

        EditExpenseCommand command = new EditExpenseCommand(
                expenseId,
                "Spotify",
                new BigDecimal("20.00"),
                Currency.EUR,
                LocalDate.now(),
                userId,
                categoryId
        );

        EditExpenseResult result = editExpense.execute(command);

        assertNotNull(result);
        assertEquals("Spotify", result.title());
        assertEquals(new BigDecimal("20.00"), result.amount());
        assertEquals(Currency.EUR, result.currency());

        verify(expenseRepository).save(expense);
    }

    @Test
    void shouldThrowWhenExpenseDoesNotExist() {
        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.empty());

        EditExpenseCommand command = new EditExpenseCommand(
                expenseId,
                "Spotify",
                new BigDecimal("20.00"),
                Currency.USD,
                LocalDate.now(),
                userId,
                UUID.randomUUID()
        );

        assertThrows(
                ExpenseNotFoundException.class,
                () -> editExpense.execute(command)
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCategoryDoesNotExist() {
        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.create(
                "Netflix",
                Money.of(new BigDecimal("10.00"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId
        );

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(expense));

        when(categoryRepository.findActiveById(categoryId))
                .thenReturn(Optional.empty());

        EditExpenseCommand command = new EditExpenseCommand(
                expenseId,
                "Spotify",
                new BigDecimal("20.00"),
                Currency.USD,
                LocalDate.now(),
                userId,
                categoryId
        );

        assertThrows(
                CategoryNotFoundException.class,
                () -> editExpense.execute(command)
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenExpenseIsNotEditable() {
        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.reconstitute(
                expenseId,
                "Netflix",
                Money.of(new BigDecimal("10.00"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId,
                Instant.now().minus(Duration.ofHours(3)),
                Instant.now().minus(Duration.ofHours(3))
        );

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(expense));

        when(categoryRepository.findActiveById(categoryId))
                .thenReturn(Optional.of(mock(Category.class)));

        EditExpenseCommand command = new EditExpenseCommand(
                expenseId,
                "Spotify",
                new BigDecimal("20.00"),
                Currency.USD,
                LocalDate.now(),
                userId,
                categoryId
        );

        assertThrows(
                ExpenseNotEditableException.class,
                () -> editExpense.execute(command)
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenExpenseDateIsFuture() {
        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.create(
                "Netflix",
                Money.of(new BigDecimal("10.00"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId
        );

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(expense));

        when(categoryRepository.findActiveById(categoryId))
                .thenReturn(Optional.of(mock(Category.class)));

        EditExpenseCommand command = new EditExpenseCommand(
                expenseId,
                "Spotify",
                new BigDecimal("20.00"),
                Currency.USD,
                LocalDate.now().plusDays(1),
                userId,
                categoryId
        );

        assertThrows(
                com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidExpenseDateException.class,
                () -> editExpense.execute(command)
        );

        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAmountIsInvalid() {
        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.create(
                "Netflix",
                Money.of(new BigDecimal("10.00"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId
        );

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(expense));

        when(categoryRepository.findActiveById(categoryId))
                .thenReturn(Optional.of(mock(Category.class)));

        EditExpenseCommand command = new EditExpenseCommand(
                expenseId,
                "Spotify",
                new BigDecimal("-20.00"),
                Currency.USD,
                LocalDate.now(),
                userId,
                categoryId
        );

        assertThrows(
                com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidAmountException.class,
                () -> editExpense.execute(command)
        );

        verify(expenseRepository, never()).save(any());
    }
}
