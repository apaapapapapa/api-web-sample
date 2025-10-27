package com.example.sample;

import java.util.logging.Logger;

/**
 * サンプルアプリケーションのエントリポイントです。
 * 実際の処理はログ出力のみで、セットアップ確認用に利用します。
 */
public final class SampleApplication {
    
    private static final Logger LOGGER = Logger.getLogger(SampleApplication.class.getName());

    private SampleApplication() {
    }

    /**
     * アプリケーションを起動し、ログへあいさつメッセージを出力します。
     *
     * @param args コマンドライン引数（未使用）
     */
    public static void main(final String[] args) {
        LOGGER.info("Hello World!");
    }
}
