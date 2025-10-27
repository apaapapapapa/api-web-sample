package com.example.sample.exception;

/**
 * 業務上のルール違反を表す実行時例外です。
 * 画面に表示したいメッセージをそのまま保持します。
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 表示用メッセージを指定して例外を生成します。
     *
     * @param message 利用者へ伝える内容
     */
    public BusinessException(final String message) {
        super(message);
    }
}
