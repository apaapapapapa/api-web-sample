package com.example.playwright;

import java.util.function.Consumer;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Minimal Playwright smoke tests that exercise the sample screens when Playwright is enabled.
 */
class BasicPlaywrightTest {

    private static final String PROPERTY_ENABLED = "playwright.enabled";
    private static final String PROPERTY_HEADLESS = "playwright.headless";
    private static final String PROPERTY_BASE_URL = "playwright.baseUrl";

    private static final String DEFAULT_BASE_URL = "https://example.com";
    private static final String DETAIL_PAGE_SUFFIX = "detail-list.xhtml";
    private static final String DEFAULT_USER_ID = "user1";
    private static final String FILTER_USER_ID = "user2";
    private static final String STATUS_REQUESTED = "REQUESTED";

    private static final String DATA_TABLE_ROWS = "#form\\:dataTable tbody tr";
    private static final String USER_SELECT = "#form\\:userSelect";
    private static final String STATUS_SELECT = "#form\\:status";
    private static final String MESSAGE_AREA = "#form\\:msgs";
    private static final String RELOAD_BUTTON_LABEL = "再読込";
    private static final String MESSAGE_RELOADED = "最新の一覧に更新しました。";

    @Test
    void shouldOpenConfiguredPageAndCheckTitle() {
        runInBrowser(page -> {
            page.navigate(baseUrl());

            Assertions.assertFalse(
                    page.title().isBlank(),
                    "ページのタイトルが空の場合は画面が正しく表示されていない可能性があります。");
        });
    }

    @Test
    void shouldFilterDetailListAndReload() {
        runInBrowser(page -> {
            page.navigate(resolveDetailListUrl());
            page.waitForSelector(DATA_TABLE_ROWS);

            final Locator rows = page.locator(DATA_TABLE_ROWS);

            page.locator(USER_SELECT).selectOption(FILTER_USER_ID);
            assertThat(rows).hasCount(2);
            assertThat(rows.first()).containsText("申請中");
            assertThat(rows.nth(1)).containsText("承認済み");

            page.locator(STATUS_SELECT).selectOption(STATUS_REQUESTED);
            assertThat(rows).hasCount(1);
            assertThat(rows.first()).containsText("申請中");

            page.getByRole(
                    AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(RELOAD_BUTTON_LABEL)
            ).click();

            final Locator messages = page.locator(MESSAGE_AREA);
            assertThat(messages).containsText(MESSAGE_RELOADED);

            assertThat(rows).hasCount(1);
            assertThat(rows.first()).containsText("申請中");
        });
    }

    private void runInBrowser(final Consumer<Page> scenario) {
        Assumptions.assumeTrue(
                Boolean.getBoolean(PROPERTY_ENABLED),
                "Playwright の統合テストを実行するには -Dplaywright.enabled=true を指定してください。");

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(createLaunchOptions());
             BrowserContext context = browser.newContext()) {

            final Page page = context.newPage();
            scenario.accept(page);
        }
    }

    private BrowserType.LaunchOptions createLaunchOptions() {
        return new BrowserType.LaunchOptions()
                .setHeadless(Boolean.getBoolean(PROPERTY_HEADLESS));
    }

    private static String resolveDetailListUrl() {
        final StringBuilder builder = new StringBuilder(baseUrl());
        if (!containsHtmlSuffix(builder)) {
            if (builder.charAt(builder.length() - 1) != '/') {
                builder.append('/');
            }
            builder.append(DETAIL_PAGE_SUFFIX);
        }
        if (builder.indexOf("userId=") < 0) {
            builder.append(builder.indexOf("?") >= 0 ? '&' : '?')
                    .append("userId=")
                    .append(DEFAULT_USER_ID);
        }
        return builder.toString();
    }

    private static String baseUrl() {
        return System.getProperty(PROPERTY_BASE_URL, DEFAULT_BASE_URL);
    }

    private static boolean containsHtmlSuffix(final StringBuilder builder) {
        final int length = builder.length();
        return (length >= 6 && builder.substring(length - 6).equalsIgnoreCase(".xhtml"))
                || (length >= 5 && builder.substring(length - 5).equalsIgnoreCase(".html"));
    }
}
