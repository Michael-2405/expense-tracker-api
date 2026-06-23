package com.michaelespinosa.expensetracker.auth.application.usecase;

import com.michaelespinosa.expensetracker.auth.application.command.RegisterUserCommand;
import com.michaelespinosa.expensetracker.auth.application.result.RegisterUserResult;
import com.michaelespinosa.expensetracker.auth.domain.exception.UserAlreadyExistsException;
import com.michaelespinosa.expensetracker.auth.domain.repository.UserRepository;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import com.michaelespinosa.expensetracker.shared.domain.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegisterUserTest {

    UserRepository userRepository;
    PasswordHasher passwordHasher;
    RegisterUser registerUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        registerUser = new RegisterUser(userRepository, passwordHasher);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHasher.hash(any())).thenReturn("hashed_password");

        RegisterUserCommand command = new RegisterUserCommand(
                "Michael", "Espinosa", "michael@test.com", "SecurePassword123!"
        );

        RegisterUserResult result = registerUser.execute(command);

        assertNotNull(result.id());
        assertEquals("Michael", result.firstName());
        assertEquals("michael@test.com", result.email());
        verify(userRepository).save(any());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        RegisterUserCommand command = new RegisterUserCommand(
                "Michael", "Espinosa", "michael@test.com", "SecurePassword123!"
        );

        assertThrows(UserAlreadyExistsException.class, () -> registerUser.execute(command));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNeverSaveWithInvalidEmail() {
        RegisterUserCommand command = new RegisterUserCommand(
                "Michael", "Espinosa", "not-an-email", "SecurePassword123!"
        );

        assertThrows(Exception.class, () -> registerUser.execute(command));
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).existsByEmail(any());
    }
}
