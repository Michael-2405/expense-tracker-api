package com.michaelespinosa.expensetracker.expenses.application.usecase;

import com.michaelespinosa.expensetracker.categories.domain.exception.CategoryNotFoundException;
import com.michaelespinosa.expensetracker.categories.domain.model.Category;
import com.michaelespinosa.expensetracker.categories.domain.repository.CategoryRepository;
import com.michaelespinosa.expensetracker.expenses.application.command.GetMonthlySummaryCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.GetMonthlySummaryResult;
import com.michaelespinosa.expensetracker.expenses.domain.projection.ExpenseSummaryEntry;
import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class GetMonthlySummary {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public GetMonthlySummary(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<GetMonthlySummaryResult> execute(GetMonthlySummaryCommand command) {
        List<ExpenseSummaryEntry> summaries =
                expenseRepository.summarizeByMonth(
                        command.userId(),
                        command.year(),
                        command.month()
                );

        Set<UUID> ids = summaries.stream()
                .map(ExpenseSummaryEntry::categoryId)
                .collect(Collectors.toSet());

        Map<UUID, Category> categories = categoryRepository.findActiveByIds(ids);

        return summaries.stream()
                .map(entry -> {
                    Category category = categories.get(entry.categoryId());

                    if (category == null) {
                        throw new CategoryNotFoundException();
                    }

                    return new GetMonthlySummaryResult(
                            entry.categoryId(),
                            category.name(),
                            entry.totalAmount(),
                            entry.currency()
                    );
                })
                .toList();
    }
}
