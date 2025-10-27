package com.example.sample.api.mapper;

import com.example.sample.api.dto.ApiErrorResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Fallback mapper that hides internal errors behind a generic payload.
 */
@Provider
@ApplicationScoped
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(final Throwable exception) {
        final ApiErrorResponse body = new ApiErrorResponse("An unexpected error occurred.");
        return Response.serverError()
                .entity(body)
                .build();
    }
}
