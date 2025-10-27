package com.example.playwright;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.example.playwright.page.DetailListPage;
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

    private static final String PROPERTY_BASE_URL = "playwright.baseUrl";
    private static final String PROPERTY_HEADLESS = "playwright.headless";
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    /**
     * http://localhost:8080/detail-list.xhtml にアクセスし、
     * 一覧テーブルの行が表示されるまで待機して可視状態になることを検証する。
     */
    @Test
    void shouldDisplayDetailListPageOnLocalhost() {
        // ローカル環境で起動したアプリケーションへ直接ナビゲートする。
        runInBrowser(page -> {
            DetailListPage detailListPage = DetailListPage.navigateTo(page, getBaseUrl());
            detailListPage.waitForDataRows();
            // 確認対象のテーブル行が表示されていることをアサートする。
            assertThat(detailListPage.firstDataRow()).isVisible();
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

    private String getBaseUrl() {
        return System.getProperty(PROPERTY_BASE_URL, DEFAULT_BASE_URL);
    }
}
