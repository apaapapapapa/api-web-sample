package com.example.sample.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 申請が正常に受け付けられたことを伝えるためのシンプルなレスポンスボディです。
 * メッセージ文字列だけを持ち、画面やクライアントに分かりやすい結果を返します。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyResponse {

    /** 利用者に表示する完了メッセージ。 */
    private String message;
}
