package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.expenses.application.command.ListExpensesCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.ListExpensesResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.InvalidExpenseFilterException;
import com.michaelespinosa.expensetracker.expenses.domain.filter.ExpenseFilter;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ListExpenses {

    private final ExpenseRepository expenseRepository;

    public ListExpenses(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ListExpensesResult> execute(ListExpensesCommand command) {
        boolean hasPeriod = command.period() != null;
        boolean hasCustomRange =
                command.startDate() != null || command.endDate() != null;

        if (hasPeriod && hasCustomRange) {
            throw new InvalidExpenseFilterException();
        }

        if (command.startDate() == null ^ command.endDate() == null) {
            throw new InvalidExpenseFilterException();
        }

        LocalDate startDate;
        LocalDate endDate;

        startDate = command.startDate();
        endDate = command.endDate();

        if (hasPeriod) {
            endDate = LocalDate.now();

            switch (command.period()) {
                case LAST_WEEK -> startDate = endDate.minusDays(7);
                case LAST_MONTH -> startDate = endDate.minusDays(30);
                case LAST_3_MONTHS -> startDate = endDate.minusDays(90);
            }
        }

        ExpenseFilter filter = new ExpenseFilter(
                startDate,
                endDate,
                command.categoryId()
        );

        List<Expense> expenses =
                expenseRepository.findByUserIdAndFilters(command.userId(), filter);

        return expenses.stream()
                .map(expense -> new ListExpensesResult(
                        expense.id(),
                        expense.title(),
                        expense.cost().amount(),
                        expense.cost().currency(),
                        expense.expenseDate(),
                        expense.categoryId(),
                        expense.createdAt(),
                        expense.updatedAt()
                ))
                .toList();
    }
}
