package com.example.sample.service;

import java.util.List;

import com.example.sample.dto.DetailRowView;
import com.example.sample.model.Status;

/**
 * 明細に関する業務処理を定義するサービスインターフェースです。
 */
public interface DetailService {

    /**
     * ログイン中のユーザーに紐づく明細一覧を取得します。
     *
     * @param userId ログインユーザーのID
     * @param status 絞り込みに利用する状態（nullならすべて）
     * @return 画面表示用に整形された明細一覧
     */
    List<DetailRowView> getListForLoginUser(String userId, Status status);

    /**
     * 選択された明細をまとめて申請状態に更新します。
     *
     * @param selectedDetailIds 申請対象の明細ID一覧
     * @param userId 操作しているユーザーID
     */
    void apply(List<Long> selectedDetailIds, String userId);
}