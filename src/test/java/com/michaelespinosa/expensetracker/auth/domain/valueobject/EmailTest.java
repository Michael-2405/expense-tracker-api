package com.michaelespinosa.expensetracker.auth.domain.valueobject;

import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = Email.of("michael@test.com");
        assertEquals("michael@test.com", email.value());
    }

    @Test
    void shouldNormalizeToLowercase(){
        Email email = Email.of("MICHAEL@TEST.COM");
        assertEquals("michael@test.com", email.value());
    }

    @Test
    void shouldThrowOnInvalidFormat(){
        assertThrows(InvalidEmailException.class, () -> Email.of("not-an-email"));
    }

    @Test
    void shouldThrowOnNull() {
        assertThrows(InvalidEmailException.class, () -> Email.of(null));
    }

    @Test
    void shouldThrowOnBlank() {
        assertThrows(InvalidEmailException.class, () -> Email.of("   "));
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Email a = Email.of("michael@test.com");
        Email b = Email.of("michael@test.com");
        assertEquals(a, b);
    }
}
