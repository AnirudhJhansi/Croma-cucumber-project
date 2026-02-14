
package com.anirudh.croma.core;

import com.anirudh.croma.exceptions.FrameworkException;
import com.anirudh.croma.utils.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        int seconds = Integer.parseInt(ConfigLoader.getInstance().get("explicitWait", "15"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    protected WebElement waitVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new FrameworkException("Element not visible: " + locator, e);
        }
    }

    protected WebElement waitClickable(By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new FrameworkException("Element not clickable: " + locator, e);
        }
    }

    protected void click(By locator) {
        try {
            WebElement el = waitClickable(locator);
            el.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            throw new FrameworkException("Failed clicking: " + locator, e);
        }
    }

    protected void type(By locator, String text) {
        try {
            WebElement el = waitVisible(locator);
            el.clear();
            el.sendKeys(text);
        } catch (InvalidElementStateException e) {
            throw new FrameworkException("Failed typing into: " + locator, e);
        }
    }

    protected boolean exists(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
