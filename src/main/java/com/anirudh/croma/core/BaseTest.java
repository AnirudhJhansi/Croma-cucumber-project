
package com.anirudh.croma.core;
import org.testng.Assert;
import com.anirudh.croma.utils.ConfigLoader;
import com.anirudh.croma.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;


/**
 * Base class offering common helpers for tests/pages.
 */
public abstract class BaseTest {
    protected WebDriver driver;
    protected ConfigLoader config = ConfigLoader.getInstance();

    protected void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    protected void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    protected byte[] takeScreenshot() {
        return ScreenshotUtil.takeScreenshotAsBytes(driver);
    }
}
