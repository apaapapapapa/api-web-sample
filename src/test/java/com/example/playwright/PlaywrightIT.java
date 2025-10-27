package com.example.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Basic Playwright end-to-end smoke test. The test is skipped unless
 * {@code -Dplaywright.enabled=true} is provided when running Maven to avoid
 * downloading browsers when the suite is not explicitly requested.
 */
class PlaywrightIT {

    @Test
    void openConfiguredPage() {
        Assumptions.assumeTrue(Boolean.getBoolean("playwright.enabled"),
                "Enable Playwright integration tests by setting -Dplaywright.enabled=true");

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                    .setHeadless(Boolean.getBoolean("playwright.headless"));
            Browser browser = playwright.chromium().launch(options);

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            String targetUrl = System.getProperty("playwright.baseUrl", "https://example.com");
            page.navigate(targetUrl);

            Assertions.assertFalse(page.title().isBlank(),
                    "The visited page should expose a non-empty title");
        }
    }

    @Test
    void filterAndReloadDetailList() {
        Assumptions.assumeTrue(Boolean.getBoolean("playwright.enabled"),
                "Enable Playwright integration tests by setting -Dplaywright.enabled=true");

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                    .setHeadless(Boolean.getBoolean("playwright.headless"));
            Browser browser = playwright.chromium().launch(options);

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate(resolveDetailListUrl());

            page.waitForSelector("#form\\:dataTable tbody tr");

            Locator rows = page.locator("#form\\:dataTable tbody tr");

            Locator userSelect = page.locator("#form\\:userSelect");
            userSelect.selectOption("user2");

            assertThat(rows).hasCount(2);
            assertThat(rows.first()).containsText("申請中");
            assertThat(rows.nth(1)).containsText("承認済み");

            page.locator("#form\\:status").selectOption("REQUESTED");

            assertThat(rows).hasCount(1);
            assertThat(rows.first()).containsText("申請中");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("再読込")).click();

            Locator messages = page.locator("#form\\:msgs");
            assertThat(messages).containsText("最新の一覧に更新しました。");

            assertThat(rows).hasCount(1);
            assertThat(rows.first()).containsText("申請中");
        }
    }

    private static String resolveDetailListUrl() {
        String targetUrl = System.getProperty("playwright.baseUrl", "https://example.com");
        if (!targetUrl.contains(".xhtml") && !targetUrl.contains(".html")) {
            if (!targetUrl.endsWith("/")) {
                targetUrl += "/";
            }
            targetUrl += "detail-list.xhtml";
        }
        if (!targetUrl.contains("userId=")) {
            targetUrl += targetUrl.contains("?") ? "&" : "?";
            targetUrl += "userId=user1";
        }
        return targetUrl;
    }
}
