package com.example.sample.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.sample.api.dto.ApiErrorResponse;
import com.example.sample.api.exception.InvalidRequestException;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class InvalidRequestExceptionMapperTest {

    private final InvalidRequestExceptionMapper mapper = new InvalidRequestExceptionMapper();

    @Test
    void mapInvalidRequestToBadRequest() {
        // 正常系: 入力エラーがHTTP400に変換されること
        final InvalidRequestException ex = new InvalidRequestException("入力エラー");

        final Response response = mapper.toResponse(ex);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiErrorResponse);
        final ApiErrorResponse body = (ApiErrorResponse) response.getEntity();
        assertEquals("入力エラー", body.getMessage());
    }
}
