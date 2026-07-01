package com.michaelespinosa.expensetracker.auth.domain.repository;

import com.michaelespinosa.expensetracker.auth.domain.model.User;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
