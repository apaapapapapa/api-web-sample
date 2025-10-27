package com.example.sample.api;

import java.util.List;
import java.util.Locale;

import com.example.sample.api.dto.ApplyRequest;
import com.example.sample.api.dto.ApplyResponse;
import com.example.sample.api.dto.DetailResponse;
import com.example.sample.api.exception.InvalidRequestException;
import com.example.sample.model.Status;
import com.example.sample.service.DetailService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

/**
 * REST API that exposes detail operations.
 */
@Path("/details")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class DetailResource {

    private final DetailService detailService;

    @Inject
    public DetailResource(final DetailService detailService) {
        this.detailService = detailService;
    }

    @GET
    public List<DetailResponse> listDetails(
            @QueryParam("userId") final String userId,
            @QueryParam("status") final String rawStatus) {
        final Status status = parseStatus(rawStatus);
        return detailService.getListForLoginUser(userId, status)
                .stream()
                .map(DetailResponse::fromDetailRowView)
                .toList();
    }

    @POST
    @Path("/apply")
    public ApplyResponse apply(final ApplyRequest request) {
        if (request == null) {
            throw new InvalidRequestException("request body is required");
        }
        detailService.apply(request.getDetailIds(), request.getUserId());
        return new ApplyResponse("Request accepted.");
    }

    private static Status parseStatus(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            return Status.valueOf(normalized);
        } catch (final IllegalArgumentException ex) {
            throw new InvalidRequestException("Unknown status: " + value);
        }
    }
}
