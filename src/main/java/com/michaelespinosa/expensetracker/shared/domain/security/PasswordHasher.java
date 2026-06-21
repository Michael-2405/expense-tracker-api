package com.michaelespinosa.expensetracker.shared.domain.security;

public interface PasswordHasher {
    String hash(String plainPassword);
    boolean verify(String plainPassword, String hashedPassword);
}
