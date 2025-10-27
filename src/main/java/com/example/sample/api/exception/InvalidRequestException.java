package com.example.sample.api.exception;

/**
 * クライアントから受け取ったリクエスト内容に問題があることを表す例外です。
 * 入力チェックで弾きたいときに投げると、400エラーとしてハンドリングされます。
 */
public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * エラーメッセージを指定して例外を生成します。
     *
     * @param message 利用者に伝えたい内容
     */
    public InvalidRequestException(final String message) {
        super(message);
    }
}
