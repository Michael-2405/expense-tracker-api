package com.michaelespinosa.expensetracker.expenses.presentation.resource;

import com.michaelespinosa.expensetracker.expenses.application.command.*;
import com.michaelespinosa.expensetracker.expenses.application.result.*;
import com.michaelespinosa.expensetracker.expenses.application.usecase.*;
import com.michaelespinosa.expensetracker.expenses.domain.filter.Period;
import com.michaelespinosa.expensetracker.expenses.presentation.request.CreateExpenseRequest;
import com.michaelespinosa.expensetracker.expenses.presentation.request.EditExpenseRequest;
import com.michaelespinosa.expensetracker.expenses.presentation.response.*;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;
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

    @Inject
    ListExpenses listExpenses;

    @Inject
    GetMonthlySummary getMonthlySummary;

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

    @GET
    @Authenticated
    public Response list(
            @QueryParam("period") Period period, @QueryParam("startDate") LocalDate startDate,
            @QueryParam("endDate") LocalDate endDate, @QueryParam("categoryId") UUID categoryId
    ) {
        UUID userId = UUID.fromString(
                securityIdentity.getPrincipal().getName()
        );

        ListExpensesCommand command = new ListExpensesCommand(
                userId,
                period,
                startDate,
                endDate,
                categoryId
        );

        List<ListExpensesResult> result = listExpenses.execute(command);

        List<ListExpensesResponse> response = result.stream()
                .map(expense -> new ListExpensesResponse(
                        expense.id(),
                        expense.title(),
                        expense.amount(),
                        expense.currency(),
                        expense.expenseDate(),
                        expense.categoryId(),
                        expense.createdAt(),
                        expense.updatedAt()
                ))
                .toList();

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Authenticated
    @Path("/summary")
    public Response getMonthlySummary(
            @QueryParam("year") @NotNull Integer year,
            @QueryParam("month") @NotNull @Min(1) @Max(12) Integer month
    ) {
        UUID userId = UUID.fromString(
                securityIdentity.getPrincipal().getName()
        );

        GetMonthlySummaryCommand command = new GetMonthlySummaryCommand(
                userId,
                year,
                month
        );

        List<GetMonthlySummaryResult> result = getMonthlySummary.execute(command);

        List<GetMonthlySummaryResponse> response = result.stream()
                .map(summary -> new GetMonthlySummaryResponse(
                        summary.categoryId(),
                        summary.categoryName(),
                        summary.currency(),
                        summary.totalAmount()
                ))
                .toList();

        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }
}
