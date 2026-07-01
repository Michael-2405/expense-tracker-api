package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.categories.domain.exception.CategoryNotFoundException;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.expenses.application.command.CreateExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.CreateExpenseResult;
import com.michaelespinosa.expensetracker.expenses.domain.model.Expense;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import com.michaelespinosa.expensetracker.expenses.domain.valueobject.Money;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateExpense {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public CreateExpense(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CreateExpenseResult execute(CreateExpenseCommand command) {
        categoryRepository.findActiveById(command.categoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Money money = Money.of(command.amount(), command.currency());

        Expense expense = Expense.create(
                command.title(),
                money,
                command.expenseDate(),
                command.userId(),
                command.categoryId()
        );

        expenseRepository.save(expense);

        return new CreateExpenseResult(
                expense.title(),
                expense.cost().amount(),
                expense.cost().currency(),
                expense.expenseDate(),
                expense.categoryId(),
                expense.createdAt()
        );
    }
}
