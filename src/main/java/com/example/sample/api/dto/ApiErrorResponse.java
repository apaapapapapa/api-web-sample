package com.example.sample.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * APIでエラーが発生した際に返す共通のレスポンスです。
 * まずはエラーメッセージだけを返すシンプルな構成になっています。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    /** 利用者に伝えるエラーメッセージ。 */
    private String message;
}
