package com.example.playwright.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * 詳細一覧ページを操作するための Page Object。
 */
public class DetailListPage {

    private static final String DATA_TABLE_ROWS = "#form\\:dataTable tbody tr";
    private static final String PAGE_PATH = "/detail-list.xhtml";

    private final Page page;

    private DetailListPage(final Page page) {
        this.page = page;
    }

    /**
     * 指定されたベース URL の詳細一覧ページへ遷移する。
     *
     * @param page    Playwright のページインスタンス
     * @param baseUrl テスト対象アプリケーションのベース URL
     * @return 遷移後の詳細一覧ページオブジェクト
     */
    public static DetailListPage navigateTo(final Page page, final String baseUrl) {
        page.navigate(composeUrl(baseUrl));
        return new DetailListPage(page);
    }

    /**
     * 一覧テーブルの行が表示されるまで待機する。
     */
    public void waitForDataRows() {
        page.waitForSelector(DATA_TABLE_ROWS);
    }

    /**
     * 一覧テーブルの先頭行を表すロケーターを返す。
     *
     * @return 一覧テーブルの先頭行ロケーター
     */
    public Locator firstDataRow() {
        return page.locator(DATA_TABLE_ROWS).first();
    }

    private static String composeUrl(final String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + PAGE_PATH;
        }
        return baseUrl + PAGE_PATH;
    }
}
