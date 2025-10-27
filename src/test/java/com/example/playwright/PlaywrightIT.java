package com.example.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
