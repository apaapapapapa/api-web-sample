package com.example.sample.api.mapper;

import com.example.sample.api.dto.ApiErrorResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * 想定外の例外を受け取ったときに実行されるフォールバックのマッパーです。
 * 内部情報を隠しつつ、利用者には共通メッセージのみを返します。
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
