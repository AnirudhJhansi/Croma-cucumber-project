package com.anirudh.croma.pages;

import com.anirudh.croma.core.BasePage;
import com.anirudh.croma.exceptions.FrameworkException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class SearchResultsPage extends BasePage {

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public void applyPriceRange(String rangeText) {
        log.info("Selecting price range: {}", rangeText);

        // 1) Click the "Price" button
        waitVisible(By.xpath("//div[@id='panel3bh-header']"));
        By PRICE_BTN = By.xpath("//div[@id='panel3bh-header']");
        click(PRICE_BTN);

        
        waitVisible(By.xpath("//div[contains(@class,'MuiPaper-root')]"));

        // 3) Price label inside modal
        By PRICE_OPTION = By.xpath(
                "//label[contains(translate(normalize-space(.)," +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," +
                "'" + rangeText.toLowerCase() + "')]"
        );

        if (!exists(PRICE_OPTION)) {
            throw new FrameworkException("Price option not found: " + rangeText);
        }

        // click the label
        click(PRICE_OPTION);

        // 4) Click Apply
        By APPLY_BTN = By.xpath("//*[@id=\"panel3bh-header\"]/div[1]/p");
        click(APPLY_BTN);

        log.info("Price range applied successfully.");
    }
    
   
    /** Try to sort results to get the 'best' on top (if available). */
    public void trySortBestSelling() {
        log.info("Trying to apply sort: Best Selling/Popular");
        try {
            By sortDrop = By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]/div[2]/div/div/div[4]/div[3]/div/div/div[2]/ul/li[6]");
            if (exists(sortDrop)) {
                click(sortDrop);
                By best = By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]/div[2]/div/div/div[4]/div[3]/div/div/div[2]/ul/li[7]");
                if (exists(best)) {
                    click(best);
                }
            }
        } catch (Exception e) {
            log.warn("Sort not applied: {}", e.getMessage());
        }
    }

    public String getFirstProductName() {
        By[] nameLocators = new By[]{
                By.cssSelector("a.product-title, h3.product-title, h2.product-title"),
        };
        for (By by : nameLocators) {
            try {
                List<WebElement> els = driver.findElements(by);
                if (!els.isEmpty()) {
                    String name = els.get(0).getText().trim();
                    if (!name.isEmpty()) return name;
                }
            } catch (Exception ignored) {}
        }
        throw new FrameworkException("Could not read first product name.");
    }

    public int getFirstProductPrice() {
        By[] priceLocators = new By[]{
            By.cssSelector(".product-price, .amount, .price, span[data-testid*='price']"),
        };
        for (By by : priceLocators) {
            try {
                List<WebElement> els = driver.findElements(by);
                for (WebElement el : els) {
                    String txt = el.getText().replaceAll("[^0-9]", "");
                    if (!txt.isEmpty()) {
                        return Integer.parseInt(txt);
                    }
                }
            } catch (Exception ignored) {}
        }
        throw new FrameworkException("Could not read first product price.");
    }
}