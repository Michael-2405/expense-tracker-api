package com.michaelespinosa.expensetracker.shared.domain.port;

import java.util.UUID;

public interface UserDataCleaner {
    void deleteAllUserData(UUID userId);
}
