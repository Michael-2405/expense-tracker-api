package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.categories.domain.exception.CategoryNotFoundException;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.expenses.application.command.EditExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.EditExpenseResult;
import com.michaelespinosa.expensetracker.expenses.domain.exception.ExpenseNotFoundException;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EditExpense {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public EditExpense(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public EditExpenseResult execute(EditExpenseCommand command) {

        Expense expense = expenseRepository
                .findByIdAndUserId(command.id(), command.userId())
                .orElseThrow(ExpenseNotFoundException::new);

        categoryRepository.findActiveById(command.categoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Money money = Money.of(command.amount(), command.currency());

        expense.edit(command.title(), money, command.expenseDate(), command.categoryId());

        expenseRepository.save(expense);

        return new EditExpenseResult(
                expense.id(),
                expense.title(),
                expense.cost().amount(),
                expense.cost().currency(),
                expense.expenseDate(),
                expense.categoryId(),
                expense.updatedAt()
        );
    }
}
