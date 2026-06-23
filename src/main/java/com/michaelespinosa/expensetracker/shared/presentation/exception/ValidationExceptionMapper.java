package com.michaelespinosa.expensetracker.shared.presentation.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ValidationException;
import com.michaelespinosa.expensetracker.shared.presentation.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        ErrorResponse error = new ErrorResponse(
                "Validation error",
                422,
                exception.getMessage()
        );

        return Response.status(422).entity(error).build();
    }
}
