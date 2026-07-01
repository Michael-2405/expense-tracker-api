package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.expenses.application.command.ListExpensesCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.ListExpensesResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidExpenseFilterException;
import com.michaelespinosa.expensetracker.expenses.domain.filter.ExpenseFilter;
import com.michaelespinosa.expensetracker.expenses.domain.filter.Period;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListExpensesTest {

    ExpenseRepository expenseRepository;
    ListExpenses listExpenses;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        listExpenses = new ListExpenses(expenseRepository);
    }

    @Test
    void shouldReturnAllExpenses() {

        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.create(
                "Netflix",
                Money.of(new BigDecimal("15.99"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId
        );

        when(expenseRepository.findByUserIdAndFilters(any(), any()))
                .thenReturn(List.of(expense));

        List<ListExpensesResult> result = listExpenses.execute(
                new ListExpensesCommand(
                        userId,
                        null,
                        null,
                        null,
                        null
                )
        );

        assertEquals(1, result.size());
        assertEquals("Netflix", result.getFirst().title());

        ArgumentCaptor<ExpenseFilter> captor =
                ArgumentCaptor.forClass(ExpenseFilter.class);

        verify(expenseRepository)
                .findByUserIdAndFilters(eq(userId), captor.capture());

        ExpenseFilter filter = captor.getValue();

        assertNull(filter.startDate());
        assertNull(filter.endDate());
        assertNull(filter.categoryId());
    }

    @Test
    void shouldFilterByCategory() {

        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        when(expenseRepository.findByUserIdAndFilters(any(), any()))
                .thenReturn(List.of());

        listExpenses.execute(
                new ListExpensesCommand(
                        userId,
                        null,
                        null,
                        null,
                        categoryId
                )
        );

        ArgumentCaptor<ExpenseFilter> captor =
                ArgumentCaptor.forClass(ExpenseFilter.class);

        verify(expenseRepository)
                .findByUserIdAndFilters(eq(userId), captor.capture());

        ExpenseFilter filter = captor.getValue();

        assertEquals(categoryId, filter.categoryId());
        assertNull(filter.startDate());
        assertNull(filter.endDate());
    }

    @Test
    void shouldConvertLastWeekToDateRange() {

        UUID userId = UUID.randomUUID();

        when(expenseRepository.findByUserIdAndFilters(any(), any()))
                .thenReturn(List.of());

        LocalDate today = LocalDate.now();

        listExpenses.execute(
                new ListExpensesCommand(
                        userId,
                        Period.LAST_WEEK,
                        null,
                        null,
                        null
                )
        );

        ArgumentCaptor<ExpenseFilter> captor =
                ArgumentCaptor.forClass(ExpenseFilter.class);

        verify(expenseRepository)
                .findByUserIdAndFilters(eq(userId), captor.capture());

        ExpenseFilter filter = captor.getValue();

        assertEquals(today.minusDays(7), filter.startDate());
        assertEquals(today, filter.endDate());
        assertNull(filter.categoryId());
    }

    @Test
    void shouldThrowWhenPeriodAndCustomRangeAreProvided() {

        ListExpensesCommand command = new ListExpensesCommand(
                UUID.randomUUID(),
                Period.LAST_MONTH,
                LocalDate.now().minusDays(5),
                LocalDate.now(),
                null
        );

        assertThrows(
                InvalidExpenseFilterException.class,
                () -> listExpenses.execute(command)
        );

        verify(expenseRepository, never())
                .findByUserIdAndFilters(any(), any());
    }

    @Test
    void shouldThrowWhenOnlyStartDateIsProvided() {

        ListExpensesCommand command = new ListExpensesCommand(
                UUID.randomUUID(),
                null,
                LocalDate.now().minusDays(5),
                null,
                null
        );

        assertThrows(
                InvalidExpenseFilterException.class,
                () -> listExpenses.execute(command)
        );

        verify(expenseRepository, never())
                .findByUserIdAndFilters(any(), any());
    }

    @Test
    void shouldThrowWhenOnlyEndDateIsProvided() {

        ListExpensesCommand command = new ListExpensesCommand(
                UUID.randomUUID(),
                null,
                null,
                LocalDate.now(),
                null
        );

        assertThrows(
                InvalidExpenseFilterException.class,
                () -> listExpenses.execute(command)
        );

        verify(expenseRepository, never())
                .findByUserIdAndFilters(any(), any());
    }
}
