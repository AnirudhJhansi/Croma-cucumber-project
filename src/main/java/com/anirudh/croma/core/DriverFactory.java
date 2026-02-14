
package com.anirudh.croma.core;

import com.anirudh.croma.utils.ConfigLoader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {
    private static final Logger log = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static void initDriver() {
        String browser = ConfigLoader.getInstance().get("browser");
        boolean headless = Boolean.parseBoolean(ConfigLoader.getInstance().get("headless", "false"));
        log.info("Launching browser: {} (headless={})", browser, headless);
        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--start-maximized");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            tlDriver.set(new ChromeDriver(options));
        } else {
            throw new RuntimeException("Unsupported browser: " + browser);
        }
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

//    public static void quitDriver() {
//        WebDriver d = tlDriver.get();
//        if (d != null) {
//            log.info("Quitting browser");
//            d.quit();
//            tlDriver.remove();
//        }
//    }
}
