package com.example.sample.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 画面の一覧表示で1行分の情報を扱うためのDTOです。
 * JSFのビューとバインドしやすいよう、選択フラグも持ちます。
 */
@Getter
@Setter
public class DetailRowView {

    /** 明細のID。 */
    private Long detailId;

    /** 表示するタイトル。 */
    private String title;

    /** 表示用の状態文字列。 */
    private String status;

    /** チェックボックスの選択状態。 */
    private boolean selected;

    /**
     * ID・タイトル・状態を指定してインスタンスを生成します。
     *
     * @param detailId 明細のID
     * @param title 明細タイトル
     * @param status 状態ラベル
     */
    public DetailRowView(final Long detailId, final String title, final String status) {
        this.detailId = detailId;
        this.title = title;
        this.status = status;
    }

}
