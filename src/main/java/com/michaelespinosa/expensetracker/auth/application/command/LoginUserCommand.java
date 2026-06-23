package com.michaelespinosa.expensetracker.auth.application.command;

import com.michaelespinosa.expensetracker.auth.domain.valueobject.Email;

public record LoginUserCommand (
        String email,
        String password
) {
}
