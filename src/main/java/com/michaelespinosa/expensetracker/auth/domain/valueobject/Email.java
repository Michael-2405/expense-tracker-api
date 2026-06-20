package com.michaelespinosa.expensetracker.auth.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;
import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidEmailException;

public final class Email {
    private static  final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidEmailException(rawValue);
        }

        String normalized = rawValue.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidEmailException(rawValue);
        }

        return new Email(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
