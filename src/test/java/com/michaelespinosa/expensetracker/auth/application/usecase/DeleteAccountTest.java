package com.michaelespinosa.expensetracker.auth.application.usecase;

import com.michaelespinosa.expensetracker.auth.application.command.DeleteAccountCommand;
import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidCredentialsException;
import com.michaelespinosa.expensetracker.auth.domain.exception.UserAlreadyDeletedException;
import com.michaelespinosa.expensetracker.auth.domain.model.User;
import com.michaelespinosa.expensetracker.auth.domain.repository.UserRepository;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import com.michaelespinosa.expensetracker.shared.domain.port.UserDataCleaner;
import com.michaelespinosa.expensetracker.shared.domain.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteAccountTest {

    UserRepository userRepository;
    PasswordHasher passwordHasher;
    UserDataCleaner userDataCleaner;
    DeleteAccount deleteAccount;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        userDataCleaner = mock(UserDataCleaner.class);
        deleteAccount = new DeleteAccount(userRepository, passwordHasher, userDataCleaner);
    }

    @Test
    void shouldDeleteAccountSuccessfully() {
        User testUser = User.register(
                "Alex", "Espinosa",
                Email.of("alex@test.com"),
                "hashed_password"
        );

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify(any(), any())).thenReturn(true);

        DeleteAccountCommand command = new DeleteAccountCommand(
                UUID.randomUUID(), "alex@test.com", "SecurePass123!"
        );

        deleteAccount.execute(command);

        verify(userDataCleaner).deleteAllUserData(any(UUID.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        DeleteAccountCommand command = new DeleteAccountCommand(
                UUID.randomUUID(), "notexist@test.com", "SecurePass123!"
        );

        assertThrows(InvalidCredentialsException.class,
                () -> deleteAccount.execute(command));
    }

    @Test
    void shouldThrowWhenUserIsNotActive() {
        User testUser = User.register(
                "Alex", "Espinosa",
                Email.of("alex@test.com"),
                "hashed_password"
        );
        testUser.delete();

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));

        DeleteAccountCommand command = new DeleteAccountCommand(
                UUID.randomUUID(), "alex@test.com", "SecurePass123!"
        );

        assertThrows(UserAlreadyDeletedException.class,
                () -> deleteAccount.execute(command));
    }

    @Test
    void shouldThrowWhenEmailDoesNotMatch() {
        User testUser = User.register(
                "Alex", "Espinosa",
                Email.of("alex@test.com"),
                "hashed_password"
        );

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));

        DeleteAccountCommand command = new DeleteAccountCommand(
                UUID.randomUUID(), "other@test.com", "SecurePass123!"
        );

        assertThrows(InvalidCredentialsException.class,
                () -> deleteAccount.execute(command));
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        User testUser = User.register(
                "Alex", "Espinosa",
                Email.of("alex@test.com"),
                "hashed_password"
        );

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify(any(), any())).thenReturn(false);

        DeleteAccountCommand command = new DeleteAccountCommand(
                UUID.randomUUID(), "alex@test.com", "WrongPass123!"
        );

        assertThrows(InvalidCredentialsException.class,
                () -> deleteAccount.execute(command));
    }

    @Test
    void shouldNeverDeleteDataWhenValidationFails() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        DeleteAccountCommand command = new DeleteAccountCommand(
                UUID.randomUUID(), "notexist@test.com", "SecurePass123!"
        );

        assertThrows(InvalidCredentialsException.class,
                () -> deleteAccount.execute(command));

        verify(userDataCleaner, never()).deleteAllUserData(any());
        verify(userRepository, never()).save(any());
    }
}
