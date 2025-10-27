package com.example.sample.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.sample.api.dto.ApiErrorResponse;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class UnhandledExceptionMapperTest {

    private final UnhandledExceptionMapper mapper = new UnhandledExceptionMapper();

    @Test
    void mapThrowableToServerError() {
        // 正常系: 想定外の例外が500レスポンスに変換されること
        final RuntimeException ex = new RuntimeException("unexpected");

        final Response response = mapper.toResponse(ex);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiErrorResponse);
        final ApiErrorResponse body = (ApiErrorResponse) response.getEntity();
        assertEquals("An unexpected error occurred.", body.getMessage());
    }
}
