package com.example.playwright;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright を使った最初のサンプルテスト。
 * ローカルで起動したアプリケーションにアクセスできることを前提に、
 * 詳細一覧ページが問題なく描画されるかを簡易的に確認する。
 */
class FirstPlaywrightTest {

    private static final String DATA_TABLE_ROWS = "#form\\:dataTable tbody tr";
    private static final String PROPERTY_HEADLESS = "playwright.headless";

    /**
     * http://localhost:8080/detail-list.xhtml にアクセスし、
     * 一覧テーブルの行が表示されるまで待機して可視状態になることを検証する。
     */
    @Test
    void shouldDisplayDetailListPageOnLocalhost() {
        // ローカル環境で起動したアプリケーションへ直接ナビゲートする。
        runInBrowser(page -> {
            page.navigate("http://localhost:8080/detail-list.xhtml");
            page.waitForSelector(DATA_TABLE_ROWS);
            // 確認対象のテーブル行が表示されていることをアサートする。
            assertThat(page.locator(DATA_TABLE_ROWS).first()).isVisible();
        });
    }

    /**
     * Playwright のブラウザ起動とページ生成を共通化し、
     * 渡されたテストシナリオを実行するヘルパーメソッド。
     *
     * @param scenario Playwright の Page を受け取って操作する処理
     */
    private void runInBrowser(final Consumer<Page> scenario) {

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(createLaunchOptions());
             BrowserContext context = browser.newContext()) {

            final Page page = context.newPage();
            scenario.accept(page);
        }
    }

    /**
     * テスト実行時の system property に従って、ヘッドレス実行の可否を設定する。
     *
     * @return Chromium を起動するためのオプション
     */
    private BrowserType.LaunchOptions createLaunchOptions() {
        return new BrowserType.LaunchOptions()
                .setHeadless(Boolean.getBoolean(PROPERTY_HEADLESS));
    }

}
