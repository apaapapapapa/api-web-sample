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

@ExtendWith(MockitoExtension.class)
class DetailResourceTest {

    @Mock
    private DetailService detailService;

    @InjectMocks
    private DetailResource resource;

    @BeforeEach
    void setUp() {
        resource = new DetailResource(detailService);
    }

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

    @Test
    void listDetailsRejectsUnknownStatus() {
        // 異常系: 存在しないステータスが指定された場合に例外になること
        assertThrows(InvalidRequestException.class,
                () -> resource.listDetails("user1", "unknown"));
    }

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

    @Test
    void applyRequiresBody() {
        // 異常系: ボディが無い場合に入力エラーとして扱われること
        assertThrows(InvalidRequestException.class, () -> resource.apply(null));
    }
}
