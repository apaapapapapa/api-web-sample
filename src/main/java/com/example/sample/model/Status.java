package com.example.sample.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 明細の状態を表す列挙型です。
 * 画面表示用のラベルも一緒に管理します。
 */
@Getter
@AllArgsConstructor
public enum Status {

    DRAFT("下書き"),
    REQUESTED("申請中"),
    APPROVED("承認済み");

    /** 画面に表示する日本語のラベル。 */
    private final String label;

}