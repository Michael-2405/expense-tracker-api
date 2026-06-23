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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginUserTest {

    UserRepository userRepository;
    PasswordHasher passwordHasher;
    TokenGenerator tokenGenerator;
    LoginUser loginUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        tokenGenerator = mock(TokenGenerator.class);
        loginUser = new LoginUser(userRepository, passwordHasher, tokenGenerator);
    }

    @Test
    void shouldLoginUserSuccessfully() {
        User testUser = User.register(
                "michael",
                "espinosa",
                Email.of("michael@test.com"),
                "hashed_password"
        );

        GeneratedToken generatedToken = new GeneratedToken("fake-token", Instant.now().plusSeconds(864000));

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify(any(), any())).thenReturn(true);
        when(tokenGenerator.generate(any())).thenReturn(generatedToken);


        LoginUserCommand command = new LoginUserCommand(
                "michael@test.com", "hashed_password"
        );

        LoginUserResult result = loginUser.execute(command);

        assertNotNull(result.token());
    }

    @Test
    void shouldThrowWhenUserDoesNotExists() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        LoginUserCommand command = new LoginUserCommand("noexisite@test.com", "SecurePass123!");

        assertThrows(InvalidCredentialsException.class, () -> loginUser.execute(command));
    }

    @Test
    void shouldThrowWhenUserIsNotActive() {
        User testUser = User.register(
                "michael",
                "espinosa",
                Email.of("michael@test.com"),
                "hashed_password"
        );

        testUser.delete();

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));

        LoginUserCommand command = new LoginUserCommand("michael@test.com", "hashed_password");

        assertThrows(InvalidCredentialsException.class, () -> loginUser.execute(command));
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        User testUser = User.register(
                "michael",
                "espinosa",
                Email.of("michael@test.com"),
                "hashed_password"
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify(any(), any())).thenReturn(false);

        LoginUserCommand command = new LoginUserCommand("michael@test.com", "SecurePass123!");

        assertThrows(InvalidCredentialsException.class, () -> loginUser.execute(command));
    }
}
