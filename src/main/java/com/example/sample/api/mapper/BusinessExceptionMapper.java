package com.example.sample.api.mapper;

import com.example.sample.api.dto.ApiErrorResponse;
import com.example.sample.exception.BusinessException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * 業務エラーをHTTP 400レスポンスに変換するマッパーです。
 * 画面に伝えたいメッセージをそのままレスポンスとして返します。
 */
@Provider
@ApplicationScoped
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(final BusinessException exception) {
        final ApiErrorResponse body = new ApiErrorResponse(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .build();
    }
}
