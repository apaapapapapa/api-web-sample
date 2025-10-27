package com.example.sample.web.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import com.example.sample.dto.DetailRowView;
import com.example.sample.dto.DetailSubmitForm;
import com.example.sample.exception.BusinessException;
import com.example.sample.model.Status;
import com.example.sample.service.DetailService;

/**
 * 明細一覧画面の表示と操作を担当するJSFのビューBeanです。
 * フィルターの変更や申請処理など、画面上の操作をまとめて扱います。
 */
@Named
@ViewScoped
public class DetailListBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_USERID = "user1";

    private final DetailService detailService;
    private final FacesContext facesContext;

    @Getter
    @Setter
    private transient List<DetailRowView> rows;

    @Getter
    @Setter
    private DetailSubmitForm form = new DetailSubmitForm();

    private boolean initialized;

    @Inject
    public DetailListBean(final DetailService detailService, final FacesContext facesContext) {
        this.detailService = detailService;
        this.facesContext = facesContext;
    }

    /**
     * 画面初期表示時に呼び出され、デフォルト値の設定と一覧の読み込みを行います。
     */
    @PostConstruct
    public void init() {
        String userId = form.getLoginUserId();
        if (userId == null || userId.isBlank()) {
            form.setLoginUserId(DEFAULT_USERID);
        }
        reloadRows();
    }

    /**
     * ビューの描画前イベントで初期化処理を1度だけ実行します。
     */
    public void onPreRenderView() {
        if (initialized) {
            return;
        }
        init();
        initialized = true;
    }

    /**
     * ユーザーが状態フィルターを変更したときに一覧を更新します。
     */
    public void onUserStatusFilterChange() {
        form.clearSelections();
        reloadRows();
    }

    /**
     * 「最新の情報に更新」ボタン押下時の処理です。
     */
    public void onReload() {
        reloadRows();
        addMessage(FacesMessage.SEVERITY_INFO, "最新の一覧に更新しました。");
    }

    /**
     * 画面のセレクトボックスに表示する全状態を返します。
     */
    public Status[] getAllStatuses() {
        return Status.values();
    }

    /**
     * 申請ボタン押下時の処理です。選択された明細をサービスに渡し、結果をメッセージで通知します。
     */
    public void submit() {
        try {
            final String userId = form.getLoginUserId();
            final List<Long> selectedIds = form.getSelectedIds();

            detailService.apply(selectedIds, userId);

            addMessage(FacesMessage.SEVERITY_INFO, "申請が完了しました。");
            form.clearSelections();
            reloadRows();
        } catch (final BusinessException ex) {
            addMessage(FacesMessage.SEVERITY_WARN, ex.getMessage());
        } catch (final Exception ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "エラーが発生しました。管理者に連絡してください。");
        }
    }

    // ===== private helpers =====

    /**
     * サービスから一覧情報を取得し、フォームの選択状態と同期します。
     */
    private void reloadRows() {
        final String userId = form.getLoginUserId();
        rows = detailService.getListForLoginUser(userId, form.getFilterStatus());
        syncSelectionsWithRows();
    }

    /**
     * 画面に表示している明細と選択マップの整合性を保ちます。
     */
    private void syncSelectionsWithRows() {
        final List<Long> idsOnScreen = rows.stream()
            .map(DetailRowView::getDetailId)
            .toList();
        form.getSelected().keySet().retainAll(idsOnScreen);
        idsOnScreen.forEach(id -> form.getSelected().putIfAbsent(id, false));
    }

    /**
     * FacesContextにメッセージを追加します。
     *
     * @param sev メッセージの重要度
     * @param msg 利用者に表示する本文
     */
    private void addMessage(final FacesMessage.Severity sev, final String msg) {
        facesContext.addMessage(null, new FacesMessage(sev, msg, null));
    }
}
