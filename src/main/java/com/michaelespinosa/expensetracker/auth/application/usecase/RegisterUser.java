package com.michaelespinosa.expensetracker.auth.application.usecase;

import com.michaelespinosa.expensetracker.auth.application.command.RegisterUserCommand;
import com.michaelespinosa.expensetracker.auth.application.result.RegisterUserResult;
import com.michaelespinosa.expensetracker.auth.domain.exception.UserAlreadyExistsException;
import com.michaelespinosa.expensetracker.auth.domain.model.User;
import com.michaelespinosa.expensetracker.auth.domain.repository.UserRepository;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Password;
import com.michaelespinosa.expensetracker.shared.domain.security.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RegisterUser {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUser(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public RegisterUserResult execute(RegisterUserCommand command) {
        Email email = Email.of(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email.toString());
        }

        Password password = Password.of(command.password());

        String hashedPassword = passwordHasher.hash(password.value());

        User user = User.register(command.firstName(), command.lastName(), email, hashedPassword);

        userRepository.save(user);

        return new RegisterUserResult(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.email().toString(),
                user.createdAt()
        );
    }
}
