package com.michaelespinosa.expensetracker.categories.presentation.resource;

import com.michaelespinosa.expensetracker.categories.application.result.CategoryResult;
import com.michaelespinosa.expensetracker.categories.application.usecase.ListActiveCategories;
import com.michaelespinosa.expensetracker.categories.presentation.response.CategoryResponse;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    ListActiveCategories listActiveCategories;

    @GET
    @Authenticated
    public Response list(){
        List<CategoryResult> result = listActiveCategories.execute();

        List<CategoryResponse> response = result.stream()
                .map(category -> new CategoryResponse(
                        category.id(),
                        category.name(),
                        category.color()))
                .toList();
        return Response.status(Response.Status.OK).entity(response).build();
    }

}
