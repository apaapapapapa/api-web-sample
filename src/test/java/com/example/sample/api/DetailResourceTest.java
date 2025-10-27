package com.example.sample.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.sample.api.dto.ApplyRequest;
import com.example.sample.api.dto.ApplyResponse;
import com.example.sample.api.dto.DetailResponse;
import com.example.sample.api.exception.InvalidRequestException;
import com.example.sample.dto.DetailRowView;
import com.example.sample.model.Status;
import com.example.sample.service.DetailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * REST エンドポイント {@link DetailResource} の基本的な振る舞いを確認するテストクラスです。
 * サービス層をモック化し、一覧取得や申請処理がどのような引数・結果になるかを順に追えます。
 */
@ExtendWith(MockitoExtension.class)
class DetailResourceTest {

    @Mock
    private DetailService detailService;

    @InjectMocks
    private DetailResource resource;

    /**
     * 各テスト前にモックを差し込んだ {@link DetailResource} を用意します。
     */
    @BeforeEach
    void setUp() {
        resource = new DetailResource(detailService);
    }

    /**
     * 正しいステータスを指定した場合に、サービス結果が API レスポンスへ整形されることを確認します。
     */
    @Test
    void listDetailsReturnsMappedPayload() {
        // 正常系: ステータス指定で一覧が取得できることを検証する
        final DetailRowView row = new DetailRowView(1L, "title", "status");
        when(detailService.getListForLoginUser("user1", Status.DRAFT))
                .thenReturn(List.of(row));

        final List<DetailResponse> payload = resource.listDetails("user1", "draft");

        assertNotNull(payload);
        assertEquals(1, payload.size());
        final DetailResponse first = payload.get(0);
        assertEquals(1L, first.getDetailId());
        assertEquals("title", first.getTitle());
        assertEquals("status", first.getStatus());
        verify(detailService).getListForLoginUser("user1", Status.DRAFT);
    }

    /**
     * 未知のステータス文字列を渡した場合に、入力エラーとして例外が送出されることを確かめます。
     */
    @Test
    void listDetailsRejectsUnknownStatus() {
        // 異常系: 存在しないステータスが指定された場合に例外になること
        assertThrows(InvalidRequestException.class,
                () -> resource.listDetails("user1", "unknown"));
    }

    /**
     * 申請リクエストを受け取った際に、サービスへ正しい引数で処理を委譲することを検証します。
     */
    @Test
    void applyDelegatesToService() {
        // 正常系: 申請APIがサービスを呼び出すこと
        final ApplyRequest request = new ApplyRequest();
        request.setUserId("user1");
        request.setDetailIds(List.of(1L, 2L));

        final ApplyResponse response = resource.apply(request);

        assertEquals("Request accepted.", response.getMessage());
        verify(detailService).apply(List.of(1L, 2L), "user1");
    }

    /**
     * リクエストボディが空の場合に、入力エラーとして例外が発生することを確認します。
     */
    @Test
    void applyRequiresBody() {
        // 異常系: ボディが無い場合に入力エラーとして扱われること
        assertThrows(InvalidRequestException.class, () -> resource.apply(null));
    }
}
