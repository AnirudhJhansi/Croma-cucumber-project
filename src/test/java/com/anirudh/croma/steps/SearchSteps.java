package com.anirudh.croma.steps;

import com.anirudh.croma.core.DriverFactory;
import com.anirudh.croma.pages.HomePage;
import com.anirudh.croma.pages.SearchResultsPage;
import com.anirudh.croma.utils.ConfigLoader;
import com.anirudh.croma.utils.ExcelReader;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Map;

public class SearchSteps {
    private static final Logger log = LogManager.getLogger(SearchSteps.class);

    private WebDriver driver;
    private HomePage home;
    private SearchResultsPage results;
    private final ExcelReader excel = new ExcelReader();

    private String searchTerm;
    private int maxPrice;

    @Given("I am on Croma home page")
    public void i_am_on_croma_home_page() {
        driver = DriverFactory.getDriver();
        home = new HomePage(driver);
        results = new SearchResultsPage(driver);
        String baseUrl = ConfigLoader.getInstance().get("baseUrl");
        log.info("Base URL: {}", baseUrl);
        // Already opened in Hook; ensure
        driver.get(baseUrl);
    }

    @When("I read test data from Excel")
    public void i_read_test_data_from_excel() {
        Map<String,String> row = excel.readFirstRow("testdata/SearchData.xlsx", "SearchData");
        searchTerm = row.getOrDefault("searchTerm", "laptop");
        String priceStr = row.getOrDefault("maxPrice", "100000)");
        maxPrice = Integer.parseInt(priceStr);
        log.info("Loaded data => searchTerm='{}', maxPrice={}", searchTerm, maxPrice);
    }

    @And("I search the product and apply price filter")
    public void i_search_and_apply_price_filter() {
        home.search(searchTerm);
        results.applyPriceRange(mapMaxPriceToRange(maxPrice));
        results.trySortBestSelling();
    }

    @Then("I capture the best product and verify it is within budget")
    public void i_capture_best_product_and_verify_price() {
        String name = results.getFirstProductName();
        int price = results.getFirstProductPrice();
        log.info("Best product => {} | Price => {}", name, price);
        Assert.assertTrue(price <= maxPrice, "Price (" + price + ") should be <= maxPrice (" + maxPrice + ")");
    }
    private String mapMaxPriceToRange(int max) {
        if (max <= 30000) return "20,001 - 30,000";
        if (max <= 40000) return "30,001 - 40,000";
        if (max <= 50000) return "40,001 - 50,000";
        if (max <= 60000) return "50,001 - 60,000";
        if (max <= 70000) return "60,001 - 70,000";
        if (max <= 80000) return "70,001 - 80,000";
        if (max <= 90000) return "80,001 - 90,000";
        return "90,001 - 1,00,000";
    }
}
