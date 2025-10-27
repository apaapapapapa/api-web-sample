package com.example.sample.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.sample.api.dto.ApiErrorResponse;
import com.example.sample.exception.BusinessException;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

/**
 * {@link BusinessExceptionMapper} がビジネス例外を HTTP400 のレスポンスへ正しく変換するかを
 * 確認するテストクラスです。変換結果のステータスと本文の両方を確認し、基本的な挙動を学べます。
 */
class BusinessExceptionMapperTest {

    private final BusinessExceptionMapper mapper = new BusinessExceptionMapper();

    /**
     * BusinessException を渡したときに、HTTP400 と例外メッセージが返却されることを検証します。
     */
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
