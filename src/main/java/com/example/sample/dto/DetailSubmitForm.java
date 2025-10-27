package com.example.sample.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.sample.model.Status;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 画面の申請フォーム状態を保持するためのBacking Bean用DTOです。
 * 入力値やチェックボックスの選択状況をまとめて管理します。
 */
@Getter
@Setter
@NoArgsConstructor
public class DetailSubmitForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ログイン中のユーザーID。 */
    private String loginUserId;

    /** 絞り込みに使用する状態。 */
    private Status filterStatus;

    private Long approverId;

    private Long reviewerId;

    /** 明細IDごとの選択状態を保持するマップ。 */
    private final Map<Long, Boolean> selected = new LinkedHashMap<>();

    /**
     * 選択されている明細IDのみを抽出します。
     *
     * @return 申請対象として選ばれたIDのリスト
     */
    public List<Long> getSelectedIds() {
        return selected.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * チェックボックスの選択状態をすべて解除します。
     */
    public void clearSelections() {
        selected.replaceAll((k, v) -> false);
    }

}
