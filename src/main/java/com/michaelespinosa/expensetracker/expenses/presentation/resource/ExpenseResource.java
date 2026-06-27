package com.michaelespinosa.expensetracker.expenses.presentation.resource;

import com.michaelespinosa.expensetracker.expenses.application.command.CreateExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.CreateExpenseResult;
import com.michaelespinosa.expensetracker.expenses.application.usecase.CreateExpense;
import com.michaelespinosa.expensetracker.expenses.presentation.request.CreateExpenseRequest;
import com.michaelespinosa.expensetracker.expenses.presentation.response.CreateExpenseResponse;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/v1/expenses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExpenseResource {

    @Inject
    CreateExpense createExpense;

    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @Authenticated
    public Response create(@Valid CreateExpenseRequest request) {

        UUID userId = UUID.fromString(
                securityIdentity.getPrincipal().getName()
        );

        CreateExpenseCommand command = new CreateExpenseCommand(
                userId,
                request.title(),
                request.amount(),
                request.currency(),
                request.expenseDate(),
                request.categoryId()
        );

        CreateExpenseResult result = createExpense.execute(command);

        CreateExpenseResponse response = new CreateExpenseResponse(
                result.title(),
                result.amount(),
                result.currency(),
                result.expenseDate(),
                result.categoryId(),
                result.createdAt()
        );

        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}
