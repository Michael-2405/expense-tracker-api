package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.categories.domain.exception.CategoryNotFoundException;
import com.michaelespinosa.expensetracker.categories.domain.model.Category;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.categories.domain.valueobject.Color;
import com.michaelespinosa.expensetracker.expenses.application.command.GetMonthlySummaryCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.GetMonthlySummaryResult;
import com.michaelespinosa.expensetracker.expenses.domain.projection.ExpenseSummaryEntry;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

class GetMonthlySummaryTest {

    private ExpenseRepository expenseRepository;
    private CategoryRepository categoryRepository;
    private GetMonthlySummary useCase;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        categoryRepository = mock(CategoryRepository.class);

        useCase = new GetMonthlySummary(
                expenseRepository,
                categoryRepository
        );
    }

    @Test
    void shouldReturnMonthlySummary() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        GetMonthlySummaryCommand command =
                new GetMonthlySummaryCommand(userId, 2026, 6);

        ExpenseSummaryEntry entry =
                new ExpenseSummaryEntry(
                        categoryId,
                        Currency.USD,
                        new BigDecimal("150.50")
                );

        Category category = Category.reconstitute(
                categoryId,
                "Food",
                Color.of("#FF0000"),
                Instant.now(),
                Instant.now(),
                null
        );

        when(expenseRepository.summarizeByMonth(userId, 2026, 6))
                .thenReturn(List.of(entry));

        when(categoryRepository.findActiveByIds(Set.of(categoryId)))
                .thenReturn(Map.of(categoryId, category));

        List<GetMonthlySummaryResult> result =
                useCase.execute(command);

        assertEquals(1, result.size());

        GetMonthlySummaryResult summary = result.getFirst();

        assertEquals(categoryId, summary.categoryId());
        assertEquals("Food", summary.categoryName());
        assertEquals(Currency.USD, summary.currency());
        assertEquals(new BigDecimal("150.50"), summary.totalAmount());

        verify(expenseRepository)
                .summarizeByMonth(userId, 2026, 6);

        verify(categoryRepository)
                .findActiveByIds(Set.of(categoryId));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoExpenses() {
        UUID userId = UUID.randomUUID();

        GetMonthlySummaryCommand command =
                new GetMonthlySummaryCommand(userId, 2026, 6);

        when(expenseRepository.summarizeByMonth(userId, 2026, 6))
                .thenReturn(List.of());

        when(categoryRepository.findActiveByIds(Set.of()))
                .thenReturn(Map.of());

        List<GetMonthlySummaryResult> result =
                useCase.execute(command);

        assertTrue(result.isEmpty());

        verify(categoryRepository)
                .findActiveByIds(Set.of());
    }

    @Test
    void shouldThrowWhenCategoryDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        GetMonthlySummaryCommand command =
                new GetMonthlySummaryCommand(userId, 2026, 6);

        ExpenseSummaryEntry entry =
                new ExpenseSummaryEntry(
                        categoryId,
                        Currency.USD,
                        new BigDecimal("50")
                );

        when(expenseRepository.summarizeByMonth(userId, 2026, 6))
                .thenReturn(List.of(entry));

        when(categoryRepository.findActiveByIds(anySet()))
                .thenReturn(Map.of());

        assertThrows(
                CategoryNotFoundException.class,
                () -> useCase.execute(command)
        );
    }
}
