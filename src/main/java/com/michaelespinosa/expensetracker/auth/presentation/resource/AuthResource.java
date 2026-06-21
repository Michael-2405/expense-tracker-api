package com.michaelespinosa.expensetracker.auth.presentation.resource;

import com.michaelespinosa.expensetracker.auth.application.command.RegisterUserCommand;
import com.michaelespinosa.expensetracker.auth.application.result.RegisterUserResult;
import com.michaelespinosa.expensetracker.auth.application.usecase.RegisterUser;
import com.michaelespinosa.expensetracker.auth.presentation.request.RegisterUserRequest;
import com.michaelespinosa.expensetracker.auth.presentation.response.RegisterUserResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    RegisterUser registerUser;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(request.firstName(), request.lastName(), request.email(), request.password());
        RegisterUserResult result = registerUser.execute(command);
        RegisterUserResponse response = new RegisterUserResponse(result.id(), result.firstName(), result.lastName(), result.email(), result.createdAt(), "User registered successfully");
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
