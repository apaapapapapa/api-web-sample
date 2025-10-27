package com.example.karate;

import java.net.URI;

import com.example.karate.support.KarateTestServer;
import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Karate のフィーチャーファイルをまとめて実行するための JUnit ランナー。
 * {@code src/test/resources/karate} 以下にあるシナリオを読み込み、
 * {@code @ignore} が付いたシナリオだけをスキップする。
 */
public class KarateIT {

    private static KarateTestServer server;

    /**
     * テスト用の埋め込みサーバーを起動し、実行時に利用するベース URL を System プロパティへ設定する。
     */
    @BeforeAll
    static void startServer() {
        server = new KarateTestServer();
        final URI baseUri = server.start();
        System.setProperty("baseUrl", baseUri.toString());
    }

    /**
     * テストで使用した埋め込みサーバーを終了し、リソースを解放する。
     */
    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Karate のフィーチャーを実際に実行する。タグ {@code @ignore} が付いているシナリオは除外する。
     *
     * @return Karate の実行結果
     */
    @Karate.Test
    Karate runKarateFeatures() {
        return Karate.run("classpath:karate").tags("~@ignore");
    }
}
