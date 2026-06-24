package com.michaelespinosa.expensetracker.auth.domain.model;

import com.michaelespinosa.expensetracker.auth.domain.exception.UserAlreadyDeletedException;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final Email email;
    private final String hashedPassword;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private User(UUID id, String firstName, String lastName, Email email,
                 String hashedPassword, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static User register(String firstName, String lastName, Email email, String hashedPassword) {
        Instant now = Instant.now();
        return new User(UUID.randomUUID(), firstName, lastName, email, hashedPassword, now, now, null);
    }

    public static User reconstitute(UUID id, String firstName, String lastName, Email email,
                                    String hashedPassword, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        return new User(id, firstName, lastName, email, hashedPassword, createdAt, updatedAt, deletedAt);
    }

    public void delete() {
        if (isDeleted()) {
            throw new UserAlreadyDeletedException();
        }
        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isActive() {
        return !isDeleted();
    }

    public UUID id() { return id; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public Email email() { return email; }
    public String hashedPassword() { return hashedPassword; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant deletedAt() { return deletedAt; }
}
