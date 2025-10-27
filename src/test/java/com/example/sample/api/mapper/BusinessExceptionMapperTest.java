package com.example.sample.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.sample.api.dto.ApiErrorResponse;
import com.example.sample.exception.BusinessException;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class BusinessExceptionMapperTest {

    private final BusinessExceptionMapper mapper = new BusinessExceptionMapper();

    @Test
    void mapBusinessExceptionToBadRequest() {
        // 正常系: ビジネス例外がHTTP400に変換されること
        final BusinessException ex = new BusinessException("ビジネスエラー");

        final Response response = mapper.toResponse(ex);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiErrorResponse);
        final ApiErrorResponse body = (ApiErrorResponse) response.getEntity();
        assertEquals("ビジネスエラー", body.getMessage());
    }
}
