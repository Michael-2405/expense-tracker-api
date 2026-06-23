package com.michaelespinosa.expensetracker.auth.infrastructure.repository;

import com.michaelespinosa.expensetracker.auth.domain.model.User;
import com.michaelespinosa.expensetracker.auth.domain.repository.UserRepository;
import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PanacheUserRepository implements UserRepository {

    @Override
    public void save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity.getEntityManager().merge(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
       UserEntity entity = UserEntity.findById(id);

       if(entity == null) {
           return Optional.empty();
       }

       User user = toDomain(entity);

       return Optional.of(user);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        String emailValue = email.value();

        UserEntity entity = UserEntity.find("email", emailValue).firstResult();

        if (entity == null){
            return Optional.empty();
        }

        User user = toDomain(entity);

        return Optional.of(user);
    }

    @Override
    public boolean existsByEmail(Email email) {
        String emailValue = email.value();

        long count = UserEntity.count("email", emailValue);
        return count > 0;
    }
    
    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.email().toString(),
                user.hashedPassword(),
                user.createdAt(),
                user.updatedAt(),
                user.deletedAt()
        );
    }

    private User toDomain(UserEntity entity) {
        return User.reconstitute(
                entity.id,
                entity.firstName,
                entity.lastName,
                Email.of(entity.email),
                entity.passwordHash,
                entity.createdAt,
                entity.updatedAt,
                entity.deletedAt
        );
    }
}
