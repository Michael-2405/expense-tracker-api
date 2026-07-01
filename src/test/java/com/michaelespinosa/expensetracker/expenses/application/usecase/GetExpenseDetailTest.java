package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.expenses.application.command.GetExpenseDetailCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.GetExpenseDetailResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotFoundException;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Currency;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetExpenseDetailTest {

    ExpenseRepository expenseRepository;
    GetExpenseDetail getExpenseDetail;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        getExpenseDetail = new GetExpenseDetail(expenseRepository);
    }

    @Test
    void shouldReturnExpenseDetail() {

        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Expense expense = Expense.create(
                "Netflix",
                Money.of(new BigDecimal("15.99"), Currency.USD),
                LocalDate.now(),
                userId,
                categoryId
        );

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.of(
                        Expense.reconstitute(
                                expenseId,
                                expense.title(),
                                expense.cost(),
                                expense.expenseDate(),
                                expense.userId(),
                                expense.categoryId(),
                                expense.createdAt(),
                                expense.updatedAt()
                        )
                ));

        GetExpenseDetailResult result = getExpenseDetail.execute(
                new GetExpenseDetailCommand(expenseId, userId)
        );

        assertNotNull(result);
        assertEquals(expenseId, result.id());
        assertEquals("Netflix", result.title());
        assertEquals(new BigDecimal("15.99"), result.amount());
        assertEquals(Currency.USD, result.currency());
        assertEquals(categoryId, result.categoryId());

        verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    }

    @Test
    void shouldThrowWhenExpenseDoesNotExist() {

        UUID expenseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(expenseRepository.findByIdAndUserId(expenseId, userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ExpenseNotFoundException.class,
                () -> getExpenseDetail.execute(
                        new GetExpenseDetailCommand(expenseId, userId)
                )
        );

        verify(expenseRepository).findByIdAndUserId(expenseId, userId);
    }
}
