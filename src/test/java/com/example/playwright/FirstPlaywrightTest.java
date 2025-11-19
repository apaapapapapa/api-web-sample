package com.example.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Download;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

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
     * ファイルダウンロード操作を行い、ダウンロードされたファイルを byte[] で取得して
     * テスト実行環境上のファイルとして保存するサンプル。
     *
     * ※ 実際の画面に合わせて navigate 先とクリック対象のセレクタは調整してください。
     */
    @Disabled
    @Test
    void shouldDownloadFileAndSaveToLocalAsBytes() {
        runInBrowser(page -> {
            // ダウンロードリンク／ボタンがあるページへ遷移
            page.navigate("http://localhost:8080/download.xhtml");

            // ダウンロード開始を待ちながら、ダウンロードボタンをクリック
            Download download = page.waitForDownload(() -> {
                // 実際の画面の要素に合わせてセレクタを変更してください
                // 例: id="downloadButton" のボタンをクリック
                page.locator("#downloadButton").click();
            });

            // Download から byte[] へ読み込み
            byte[] fileBytes;
            try (InputStream in = download.createReadStream();
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                in.transferTo(buffer);
                fileBytes = buffer.toByteArray();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            // テスト実行環境上の保存先パス
            Path targetDir = Paths.get("build/downloads");
            Path targetFile = targetDir.resolve(download.suggestedFilename());

            try {
                Files.createDirectories(targetDir);
                Files.write(targetFile, fileBytes);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

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
             // ダウンロード操作も扱えるよう、acceptDownloads を true にしておく
             BrowserContext context = browser.newContext(
                     new NewContextOptions().setAcceptDownloads(true))) {

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
