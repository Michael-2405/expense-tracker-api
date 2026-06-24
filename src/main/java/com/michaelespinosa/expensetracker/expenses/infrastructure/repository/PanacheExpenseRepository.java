package com.michaelespinosa.expensetracker.expenses.infrastructure.repository;

import com.michaelespinosa.expensetracker.expenses.domain.repository.ExpenseRepository;

import java.util.UUID;

public class PanacheExpenseRepository implements ExpenseRepository {

    @Override
    public void deleteAllByUserId(UUID userId){

    }
}
