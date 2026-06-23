package com.michaelespinosa.expensetracker.shared.presentation.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.ConflictException;
import com.michaelespinosa.expensetracker.shared.presentation.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(ConflictException exception) {
        ErrorResponse error = new ErrorResponse(
                "Conflict Error",
                409,
                exception.getMessage()
        );

        return Response.status(409).entity(error).build();
    }
}
