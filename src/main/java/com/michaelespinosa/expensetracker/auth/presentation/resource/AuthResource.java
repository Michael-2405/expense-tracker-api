package com.michaelespinosa.expensetracker.auth.presentation.resource;

import com.michaelespinosa.expensetracker.auth.application.command.DeleteAccountCommand;
import com.michaelespinosa.expensetracker.auth.application.command.LoginUserCommand;
import com.michaelespinosa.expensetracker.auth.application.command.RegisterUserCommand;
import com.michaelespinosa.expensetracker.auth.application.result.LoginUserResult;
import com.michaelespinosa.expensetracker.auth.application.result.RegisterUserResult;
import com.michaelespinosa.expensetracker.auth.application.usecase.DeleteAccount;
import com.michaelespinosa.expensetracker.auth.application.usecase.LoginUser;
import com.michaelespinosa.expensetracker.auth.application.usecase.RegisterUser;
import com.michaelespinosa.expensetracker.auth.presentation.request.DeleteAccountRequest;
import com.michaelespinosa.expensetracker.auth.presentation.request.LoginUserRequest;
import com.michaelespinosa.expensetracker.auth.presentation.request.RegisterUserRequest;
import com.michaelespinosa.expensetracker.auth.presentation.response.LoginUserResponse;
import com.michaelespinosa.expensetracker.auth.presentation.response.RegisterUserResponse;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

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

    @Inject
    LoginUser loginUser;

    @POST
    @Path("/login")
    public Response login(@Valid LoginUserRequest request) {
        LoginUserCommand command = new LoginUserCommand(request.email(), request.password());
        LoginUserResult result = loginUser.execute(command);
        LoginUserResponse response = new LoginUserResponse(result.token(), result.expiresAt());
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Inject
    DeleteAccount deleteAccount;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/account/close")
    @Authenticated
    public Response close(@Valid DeleteAccountRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());

        DeleteAccountCommand command = new DeleteAccountCommand(
                userId,
                request.email(),
                request.password()
        );

        deleteAccount.execute(command);

        return Response.noContent().build();
    }
}
