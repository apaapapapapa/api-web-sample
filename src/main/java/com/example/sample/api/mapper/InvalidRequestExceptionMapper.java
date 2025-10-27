package com.example.sample.api.mapper;

import com.example.sample.api.dto.ApiErrorResponse;
import com.example.sample.api.exception.InvalidRequestException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Converts invalid request exceptions into HTTP 400 responses.
 */
@Provider
@ApplicationScoped
public class InvalidRequestExceptionMapper implements ExceptionMapper<InvalidRequestException> {

    @Override
    public Response toResponse(final InvalidRequestException exception) {
        final ApiErrorResponse body = new ApiErrorResponse(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .build();
    }
}
