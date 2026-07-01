package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.categories.domain.exception.CategoryNotFoundException;
import com.michaelespinosa.expensetracker.categories.domain.model.Category;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.expenses.application.command.CreateExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.CreateExpenseResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidAmountException;
import com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidExpenseDateException;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateExpenseTest {

    ExpenseRepository expenseRepository;
    CategoryRepository categoryRepository;
    CreateExpense createExpense;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        createExpense = new CreateExpense(expenseRepository, categoryRepository);
    }

    @Test
    void shouldCreateExpenseSuccessfully() {
        when(categoryRepository.findActiveById(any(UUID.class)))
                .thenReturn(Optional.of(mock(Category.class)));

        CreateExpenseCommand command = new CreateExpenseCommand(
                UUID.randomUUID(), "Netflix",
                new BigDecimal("15.99"), Currency.USD,
                LocalDate.now(), UUID.randomUUID()
        );

        CreateExpenseResult result = createExpense.execute(command);

        assertNotNull(result);
        assertEquals("Netflix", result.title());
        verify(expenseRepository).save(any());
    }

    @Test
    void shouldThrowWhenCategoryDoesNotExist() {
        when(categoryRepository.findActiveById(any(UUID.class)))
                .thenReturn(Optional.empty());

        CreateExpenseCommand command = new CreateExpenseCommand(
                UUID.randomUUID(), "Netflix",
                new BigDecimal("15.99"), Currency.USD,
                LocalDate.now(), UUID.randomUUID()
        );

        assertThrows(CategoryNotFoundException.class,
                () -> createExpense.execute(command));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDateIsFuture() {
        when(categoryRepository.findActiveById(any(UUID.class)))
                .thenReturn(Optional.of(mock(Category.class)));

        CreateExpenseCommand command = new CreateExpenseCommand(
                UUID.randomUUID(), "Netflix",
                new BigDecimal("15.99"), Currency.USD,
                LocalDate.now().plusDays(1), UUID.randomUUID()
        );

        assertThrows(InvalidExpenseDateException.class,
                () -> createExpense.execute(command));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAmountIsInvalid() {
        when(categoryRepository.findActiveById(any(UUID.class)))
                .thenReturn(Optional.of(mock(Category.class)));

        CreateExpenseCommand command = new CreateExpenseCommand(
                UUID.randomUUID(), "Netflix",
                new BigDecimal("-10.00"), Currency.USD,
                LocalDate.now(), UUID.randomUUID()
        );

        assertThrows(InvalidAmountException.class,
                () -> createExpense.execute(command));
        verify(expenseRepository, never()).save(any());
    }
}
