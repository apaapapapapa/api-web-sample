package com.example.sample.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 明細を申請するときにクライアントから送られてくるリクエストボディです。
 * 初心者でも扱いやすいよう、必要な情報はユーザーIDと明細IDの一覧だけです。
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplyRequest {

    /** 申請を行うユーザーのID。 */
    private String userId;

    /** 申請対象に選択した明細IDのリスト。 */
    private List<Long> detailIds;
}
