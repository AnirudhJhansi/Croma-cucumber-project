package com.anirudh.croma.pages;

import com.anirudh.croma.core.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open(String url) {
        log.info("Opening URL: {}", url);
        driver.get(url);
    }

  
    public void search(String query) {
        log.info("Searching for: {}", query);

        // 1) Try to dismiss common overlays that often cover the header/search bar.
        dismissOverlaysIfAny();

        // 2) Candidate locators for the search input.
        By[] candidates = new By[]{
        	    By.cssSelector("[data-testid='search-list'] input#searchV2[name='search']"),   // BEST & STABLE
        	    By.cssSelector("input#searchV2.search-field"),                                 // fast fallback
        	    By.cssSelector("input[name='search'][id^='searchV']"),                          // version-safe
        	    By.cssSelector("[data-testid='search-list'] input.search-field"),               // stable container
        	    By.xpath("//input[@name='search' and @id='searchV2']")                          // reliable xpath
        	};

        WebElement input = null;
        // 3) Find the first visible candidate and make sure it’s clickable.
        for (By c : candidates) {
            try {
                if (exists(c)) {
                    WebElement el = driver.findElement(c);
                    if (el.isDisplayed() && el.getRect().getHeight() > 0 && el.getRect().getWidth() > 0) {
                        
                        input = wait.until(ExpectedConditions.elementToBeClickable(el));
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
        // Fallback to a generic visible input if none matched above
        if (input == null) {
            input = waitVisible(By.cssSelector("input[type='search']"));
            input = wait.until(ExpectedConditions.elementToBeClickable(input));
        }

        // 4) Bring into view + focus (scroll & click/focus).
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);
        } catch (Exception ignored) {}

        try {
            input.click(); // focuses in most cases
        } catch (ElementClickInterceptedException e) {
            // If something briefly overlaps, try focusing via JS
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", input);
            } catch (Exception ignored) {}
        }

        // 5) Clear safely (avoid WebElement.clear() which often throws not-interactable on custom inputs)
        boolean cleared = false;
        try {
            new Actions(driver)
                    .click(input)
                    .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                    .sendKeys(Keys.DELETE)
                    .perform();
            cleared = true;
        } catch (Exception ignored) {}

        if (!cleared) {
            // JS fallback if the keyboard path didn’t work
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", input);
                cleared = true;
            } catch (Exception ignored) {}
        }

        // As a last resort, try the native clear (only if we haven’t already triggered the exception path)
        if (!cleared) {
            try {
                input.clear();
            } catch (Exception ignored) {}
        }

        // 6) Type and submit
        input.sendKeys(query);
        input.sendKeys(Keys.ENTER);

        // 7) Optional: wait until search results are present or URL indicates search
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/search"),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid*='product'], .product-grid, [class*='product']"))
            ));
        } catch (TimeoutException te) {
            log.warn("Search results page did not meet expected conditions within timeout; proceeding.");
        }
    }

    /**
     * Close cookie banners/modals if present. These are generic heuristics; tune selectors to Croma DOM if needed.
     */
    private void dismissOverlaysIfAny() {
        // Cookie accept buttons
        try {
            By acceptCookiesBtn = By.xpath(
                    "//button[contains(.,'Accept') or contains(.,'Got it') or contains(.,'I agree') or contains(.,'OK')]"
            );
            clickIfVisible(acceptCookiesBtn);
        } catch (Exception ignored) {}

        // Modal close buttons
        try {
            By modalClose = By.cssSelector(
                    ".modal [aria-label='Close'], .modal .close, .dialog .close, button[aria-label='Close']"
            );
            clickIfVisible(modalClose);
        } catch (Exception ignored) {}

        // Hide sticky widgets that can overlap header (chat bubbles, banners)
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('.chat-widget,.sticky-banner,.notification,.cookie,iframe[title*=chat]').forEach(e=>e.style.display='none');"
            );
        } catch (Exception ignored) {}
    }

    /**
     * Utility to click an element only if it's visible & clickable within a short timeout.
     * Relies on BasePage.wait (WebDriverWait).
     */
    private void clickIfVisible(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            wait.until(ExpectedConditions.elementToBeClickable(el)).click();
        } catch (TimeoutException ignored) {}
    }
}