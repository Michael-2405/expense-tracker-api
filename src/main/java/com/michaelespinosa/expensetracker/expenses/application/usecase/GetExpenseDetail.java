package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.expenses.application.command.GetExpenseDetailCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.GetExpenseDetailResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotFoundException;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetExpenseDetail {

    private final ExpenseRepository expenseRepository;

    public GetExpenseDetail(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public GetExpenseDetailResult execute(GetExpenseDetailCommand command) {
        Expense expense = expenseRepository
                .findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(ExpenseNotFoundException::new);

        return new GetExpenseDetailResult(
                expense.id(),
                expense.title(),
                expense.cost().amount(),
                expense.cost().currency(),
                expense.expenseDate(),
                expense.categoryId(),
                expense.createdAt(),
                expense.updatedAt()
        );
    }
}
