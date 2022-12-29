package com.artemkaxboy.playwright;

import java.util.regex.Pattern;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightDemoTest {

    @Test
    public void simple() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();
            page.navigate("https://playwright.dev");

            // Expect a title "to contain" a substring.
            assertThat(page).hasTitle(Pattern.compile("Playwright"));

            // create a locator
            Locator getStarted = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Get Started"));

            // Expect an attribute "to be strictly equal" to the value.
            assertThat(getStarted).hasAttribute("href", "/docs/intro");

            // Click the get started link.
            getStarted.click();

            // Expects the URL to contain intro.
            assertThat(page).hasURL(Pattern.compile(".*intro"));
        }
    }

    // ./mvnw exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="codegen demo.playwright.dev/todomvc"
    @Test
    public void generated() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.jettycloud.com");
            page.locator("div").filter(new Locator.FilterOptions().setHasText("We use cookies to analyze data.If you keep using this website, it means that you")).nth(3).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept")).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Vacancies")).first().click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("C++ developer (RCV media) Our team is involved in the development of Selective Forwarding Unit: core video conference system media backend service Locations: Georgia")).click();
            page.getByText("Corporate training programs, English language courses.").click();
            Locator contacts = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Сontacts"));
            assertThat(contacts).hasAttribute("href", "/contacts");
        }
    }
}
