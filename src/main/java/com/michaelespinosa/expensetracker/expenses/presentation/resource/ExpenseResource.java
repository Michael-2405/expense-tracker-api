package com.michaelespinosa.expensetracker.expenses.presentation.resource;

import com.michaelespinosa.expensetracker.expenses.application.command.CreateExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.command.EditExpenseCommand;
import com.michaelespinosa.expensetracker.expenses.application.command.GetExpenseDetailCommand;
import com.michaelespinosa.expensetracker.expenses.application.result.CreateExpenseResult;
import com.michaelespinosa.expensetracker.expenses.application.result.EditExpenseResult;
import com.michaelespinosa.expensetracker.expenses.application.result.GetExpenseDetailResult;
import com.michaelespinosa.expensetracker.expenses.application.usecase.CreateExpense;
import com.michaelespinosa.expensetracker.expenses.application.usecase.EditExpense;
import com.michaelespinosa.expensetracker.expenses.application.usecase.GetExpenseDetail;
import com.michaelespinosa.expensetracker.expenses.presentation.request.CreateExpenseRequest;
import com.michaelespinosa.expensetracker.expenses.presentation.request.EditExpenseRequest;
import com.michaelespinosa.expensetracker.expenses.presentation.response.CreateExpenseResponse;
import com.michaelespinosa.expensetracker.expenses.presentation.response.EditExpenseResponse;
import com.michaelespinosa.expensetracker.expenses.presentation.response.GetExpenseDetailResponse;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/v1/expenses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExpenseResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    CreateExpense createExpense;

    @Inject
    EditExpense editExpense;

    @Inject
    GetExpenseDetail getExpenseDetail;

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

    @PUT
    @Path("/{id}")
    @Authenticated
    public Response edit(@PathParam("id") UUID id, @Valid EditExpenseRequest request) {

        UUID userId = UUID.fromString(
                securityIdentity.getPrincipal().getName()
        );

        EditExpenseCommand command = new EditExpenseCommand(
                id,
                request.title(),
                request.amount(),
                request.currency(),
                request.expenseDate(),
                userId,
                request.categoryId()
        );

        EditExpenseResult result = editExpense.execute(command);

        EditExpenseResponse response = new EditExpenseResponse(
                result.id(),
                result.title(),
                result.amount(),
                result.currency(),
                result.expenseDate(),
                result.categoryId(),
                result.updatedAt()
        );

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public Response getById(@PathParam("id") UUID id) {

        UUID userId = UUID.fromString(
                securityIdentity.getPrincipal().getName()
        );

        GetExpenseDetailCommand command = new GetExpenseDetailCommand(
                id,
                userId
        );

        GetExpenseDetailResult result = getExpenseDetail.execute(command);

        GetExpenseDetailResponse response = new GetExpenseDetailResponse(
                result.id(),
                result.title(),
                result.amount(),
                result.currency(),
                result.expenseDate(),
                result.categoryId(),
                result.createdAt(),
                result.updatedAt()
        );

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }
}
