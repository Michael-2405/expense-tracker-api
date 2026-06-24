package com.michaelespinosa.expensetracker.auth.application.usecase;

import com.michaelespinosa.expensetracker.auth.application.command.DeleteAccountCommand;
import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidCredentialsException;
import com.michaelespinosa.expensetracker.auth.domain.exception.UserAlreadyDeletedException;
import com.michaelespinosa.expensetracker.auth.domain.model.User;
import com.michaelespinosa.expensetracker.auth.domain.repository.UserRepository;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import com.michaelespinosa.expensetracker.shared.domain.port.UserDataCleaner;
import com.michaelespinosa.expensetracker.shared.domain.security.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class DeleteAccount {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserDataCleaner userDataCleaner;

    public DeleteAccount(UserRepository userRepository, PasswordHasher passwordHasher, UserDataCleaner userDataCleaner) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.userDataCleaner = userDataCleaner;
    }

    @Transactional
    public void execute(DeleteAccountCommand command) {
        Email email = Email.of(command.email());

        Optional<User> user = userRepository.findById(command.userId());

        if (user.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User actualUser = user.get();

        if (!actualUser.isActive()) {
            throw new UserAlreadyDeletedException();
        }


        if (!actualUser.email().value().equals(email.value())) {
            throw new InvalidCredentialsException();
        }

        if(!passwordHasher.verify(command.password(), actualUser.hashedPassword())) {
            throw new InvalidCredentialsException();
        }

        userDataCleaner.deleteAllUserData(command.userId());
        actualUser.delete();
        userRepository.save(actualUser);
    }
}
