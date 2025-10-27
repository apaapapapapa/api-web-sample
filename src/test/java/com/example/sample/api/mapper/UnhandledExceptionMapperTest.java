package com.example.sample.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.sample.api.dto.ApiErrorResponse;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

/**
 * {@link UnhandledExceptionMapper} が想定外の例外を HTTP500 のレスポンスへ変換できることを
 * シンプルに確認するテストクラスです。
 * 初めて例外マッパーを学ぶ人でも流れが追えるよう、入力と出力の関係を明示しています。
 */
class UnhandledExceptionMapperTest {

    private final UnhandledExceptionMapper mapper = new UnhandledExceptionMapper();

    /**
     * RuntimeException を渡したときに、HTTP500 とメッセージ付きのレスポンスが返ることを検証します。
     */
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
