package com.michaelespinosa.expensetracker.expenses.infrastructure.repository;

import com.michaelespinosa.expensetracker.shared.domain.port.UserDataCleaner;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ExpenseDataCleaner  implements UserDataCleaner {

    @Override
    public void deleteAllUserData(UUID userId){

    }
}
