package com.michaelespinosa.expensetracker.shared.presentation.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.BadRequestException;
import com.michaelespinosa.expensetracker.shared.presentation.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException exception) {
        ErrorResponse error = new ErrorResponse(
                "Bad Request Error",
                400,
                exception.getMessage()
        );

        return Response.status(400).entity(error).build();
    }
}
