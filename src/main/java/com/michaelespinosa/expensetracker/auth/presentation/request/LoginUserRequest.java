package com.michaelespinosa.expensetracker.auth.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record LoginUserRequest (
        @NotBlank
        @Email
        @Schema(examples = "michael@example.com")
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        @Schema(examples = "SecurePass123!")
        String password
) {}
