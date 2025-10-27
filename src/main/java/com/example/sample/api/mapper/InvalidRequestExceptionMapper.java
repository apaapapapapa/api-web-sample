package com.example.sample.api.mapper;

import com.example.sample.api.dto.ApiErrorResponse;
import com.example.sample.api.exception.InvalidRequestException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * リクエスト内容の不備を表す例外をHTTP 400レスポンスへ変換します。
 * クライアントに何が問題だったのかをシンプルに伝えます。
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
