package com.michaelespinosa.expensetracker.shared.presentation.exception;

import com.michaelespinosa.expensetracker.shared.domain.exception.UnauthorizedException;
import com.michaelespinosa.expensetracker.shared.presentation.response.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException exception) {
        ErrorResponse error = new ErrorResponse(
                "Unauthorized error",
                401,
                exception.getMessage()
        );

        return Response.status(401).entity(error).build();
    }
}
