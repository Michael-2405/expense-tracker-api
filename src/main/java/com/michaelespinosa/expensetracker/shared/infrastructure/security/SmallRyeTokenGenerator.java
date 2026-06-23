package com.michaelespinosa.expensetracker.shared.infrastructure.security;

import com.michaelespinosa.expensetracker.shared.domain.security.GeneratedToken;
import com.michaelespinosa.expensetracker.shared.domain.security.TokenGenerator;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class SmallRyeTokenGenerator implements TokenGenerator {

    @ConfigProperty(name = "mp.jwt.issuer")
    String issuer;

    @Override
    public GeneratedToken generate(String userId) {
        Duration duration = Duration.ofHours(24);
        Instant expiresAt = Instant.now().plus(duration);

        String token = Jwt.issuer(issuer)
                .subject(userId)
                .issuedAt(System.currentTimeMillis() / 1000)
                .expiresIn(duration)
                .sign();

        return new GeneratedToken(token, expiresAt);
    }
}
