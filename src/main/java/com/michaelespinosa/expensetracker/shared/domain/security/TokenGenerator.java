package com.michaelespinosa.expensetracker.shared.domain.security;

public interface TokenGenerator {
    GeneratedToken generate(String userId);
}
