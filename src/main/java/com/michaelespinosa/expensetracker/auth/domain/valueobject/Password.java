package com.michaelespinosa.expensetracker.auth.domain.valueobject;

import com.michaelespinosa.expensetracker.auth.domain.exception.InvalidPasswordException;

public final class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 72;

    private final String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidPasswordException("Password must not be null or blank");
        }

        if (rawValue.length() < MIN_LENGTH || rawValue.length() > MAX_LENGTH) {
            throw new InvalidPasswordException(
                    "Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters"
            );
        }

        return new Password(rawValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Password password)) return false;
        return value.equals(password.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "Password[PROTECTED]";
    }
}
