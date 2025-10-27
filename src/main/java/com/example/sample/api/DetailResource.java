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
 * 明細情報を操作するためのREST APIです。
 * 画面から呼び出され、ログイン中のユーザー向けに明細一覧の取得や申請を行います。
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

    /**
     * 明細一覧を取得します。
     * <p>
     * userIdとstatusをクエリパラメーターで受け取り、ログインユーザーに表示する明細だけを返します。
     * statusは省略可能で、指定がない場合はすべての状態を検索します。
     * </p>
     *
     * @param userId 画面で操作しているユーザーID
     * @param rawStatus クエリで受け取った状態文字列（省略可）
     * @return APIレスポンスとして返却する明細一覧
     */
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

    /**
     * 申請処理を受け付けます。
     * <p>
     * 必須項目が欠けていないかをチェックし、問題がなければサービス層に処理を移譲します。
     * 正常終了時は簡単なメッセージのみを返します。
     * </p>
     *
     * @param request 申請対象の明細IDとユーザーIDを含むリクエストボディ
     * @return 処理が受け付けられたことを示すレスポンス
     */
    @POST
    @Path("/apply")
    public ApplyResponse apply(final ApplyRequest request) {
        if (request == null) {
            throw new InvalidRequestException("request body is required");
        }
        detailService.apply(request.getDetailIds(), request.getUserId());
        return new ApplyResponse("Request accepted.");
    }

    /**
     * 文字列から {@link Status} を解析します。
     * <p>
     * 入力が空の場合は null を返し、認識できない値であればバリデーションエラーに変換します。
     * </p>
     *
     * @param value クエリパラメーターから受け取った状態文字列
     * @return 対応する {@link Status}、または未指定の場合はnull
     */
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
