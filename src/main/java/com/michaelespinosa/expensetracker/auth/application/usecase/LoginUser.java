package com.michaelespinosa.expensetracker.auth.application.usecase;

import com.michaelespinosa.expensetracker.auth.application.command.LoginUserCommand;
import com.michaelespinosa.expensetracker.auth.application.result.LoginUserResult;
import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidCredentialsException;
import com.michaelespinosa.expensetracker.auth.domain.model.User;
import com.michaelespinosa.expensetracker.auth.domain.repository.UserRepository;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import com.michaelespinosa.expensetracker.shared.domain.security.GeneratedToken;
import com.michaelespinosa.expensetracker.shared.domain.security.PasswordHasher;
import com.michaelespinosa.expensetracker.shared.domain.security.TokenGenerator;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class LoginUser {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;

    public LoginUser(UserRepository userRepository, PasswordHasher passwordHasher, TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    public LoginUserResult execute(LoginUserCommand command) {
        Email email = Email.of(command.email());

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw  new InvalidCredentialsException();
        }

        User actualUser = user.get();

        if (!actualUser.isActive()) {
            throw new InvalidCredentialsException();
        }

        if(!passwordHasher.verify(command.password(), actualUser.hashedPassword())) {
            throw new InvalidCredentialsException();
        }

        GeneratedToken generated = tokenGenerator.generate(actualUser.id().toString());
        return new LoginUserResult(generated.token(), generated.expiresAt());
    }

}
