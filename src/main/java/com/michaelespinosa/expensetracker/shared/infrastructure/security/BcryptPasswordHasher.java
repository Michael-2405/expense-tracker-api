package com.michaelespinosa.expensetracker.shared.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.michaelespinosa.expensetracker.shared.domain.security.PasswordHasher;

public class BcryptPasswordHasher implements PasswordHasher {
    @Override
    public String hash(String plainPassword) {
        final int COST_FACTOR = 12;
        char[] password = plainPassword.toCharArray();
        return BCrypt.withDefaults().hashToString(COST_FACTOR, password);
    }

    @Override
    public boolean verify(String plainPassword, String hashedPassword) {
        char[] plain = plainPassword.toCharArray();

        BCrypt.Result result = BCrypt.verifyer().verify(plain, hashedPassword);

        return result.verified;
    }
}
