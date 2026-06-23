package com.michaelespinosa.expensetracker.auth.domain.valueobject;

import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest {

    @Test
    void shouldCreateValidPassword() {
        assertDoesNotThrow(() -> Password.of("SecurePass123!"));
    }

    @Test
    void shouldThrowOnTooShort() {
        assertThrows(InvalidPasswordException.class, () -> Password.of("abc"));
    }

    @Test
    void shouldThrowOnTooLong() {
        String tooLong = "a".repeat(75);
        assertThrows(InvalidPasswordException.class, () -> Password.of(tooLong));
    }

    @Test
    void shouldThrowOnNull() {
        assertThrows(InvalidPasswordException.class, () -> Password.of(null));
    }

    @Test
    void shouldThrowOnBlank() {
        assertThrows(InvalidPasswordException.class, () -> Password.of("    "));
    }

    @Test
    void shouldNotExposeValueInToString() {
        Password password = Password.of("SecurePass123!");
        assertFalse(password.toString().contains("SecurePassword123!"));
    }
}
