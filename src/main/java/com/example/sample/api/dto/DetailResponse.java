package com.example.sample.api.dto;

import com.example.sample.dto.DetailRowView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 明細情報を画面に返すためのレスポンスDTOです。
 * 数値ID・タイトル・状態といった最低限の項目のみを含めています。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {

    /** 表示対象の明細ID。 */
    private Long detailId;

    /** 明細のタイトル。 */
    private String title;

    /** 文字列表現の状態（例：申請中）。 */
    private String status;

    /**
     * サービス層で利用している行表示用クラスからDTOへ変換します。
     * <p>
     * プロパティ名の違いを意識せずに済むよう、同じ順番で値をコピーします。
     * </p>
     *
     * @param rowView 画面表示用にまとめられた明細データ
     * @return APIレスポンス用のDTO
     */
    public static DetailResponse fromDetailRowView(final DetailRowView rowView) {
        return new DetailResponse(rowView.getDetailId(), rowView.getTitle(), rowView.getStatus());
    }
}
