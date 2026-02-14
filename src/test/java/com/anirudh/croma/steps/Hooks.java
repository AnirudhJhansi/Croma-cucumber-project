
package com.anirudh.croma.steps;

import com.anirudh.croma.core.DriverFactory;
import com.anirudh.croma.utils.ConfigLoader;
import com.anirudh.croma.utils.ScreenshotUtil;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class Hooks {
    private static final Logger log = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        log.info("=== START Scenario: {} ===", scenario.getName());
        DriverFactory.initDriver();
        WebDriver driver = DriverFactory.getDriver();
        
        String url = ConfigLoader.getInstance().get("baseUrl");
        driver.get(url);
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] png = ScreenshotUtil.takeScreenshotAsBytes(DriverFactory.getDriver());
                if (png != null && png.length > 0) {
                    scenario.attach(png, "image/png", "Failure Screenshot");
                    Allure.addAttachment("Failure Screenshot", "image/png", new java.io.ByteArrayInputStream(png), ".png");
                }
            }
        } finally {
            
            log.info("=== END Scenario: {} | Status: {} ===", scenario.getName(), scenario.getStatus());
        }
    }
}
