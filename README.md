
# Croma Cucumber POM Framework (Allure, Log4j2, Excel, Screenshots)

This is a ready-to-run Selenium-Cucumber-TestNG automation framework that:

- ✅ Uses **Page Object Model (POM)** and a **Base class**
- ✅ Reads **test data from Excel (Apache POI)** and **config from properties**
- ✅ Integrates **Log4j2** logging and **Allure** reporting
- ✅ Provides **assertions**, **exception handling**, and **screenshots on failure**
- ✅ Demonstrates an example on **croma.com**: search for **Laptop**, apply **Max Price** filter, and capture the **best (first) result**

## Prerequisites
- JDK 11+
- Maven 3.8+
- Chrome browser
- (Optional for reports) **Allure CLI** installed and added to PATH

## How to Run
```bash
mvn clean test -Dcucumber.filter.tags=@smoke
```

## Generate Allure Report
```bash
# After tests create allure-results in target/
allure serve target/allure-results
# or
allure generate target/allure-results -o target/allure-report --clean
```

## Where to Edit Data
- **Config**: `src/test/resources/config/config.properties`
- **Excel**: `src/test/resources/testdata/SearchData.xlsx` (Sheet: `SearchData`)
  - Columns: `searchTerm`, `maxPrice`

## Notes
- DOM of croma.com can change; if a locator breaks, update the locator inside the respective Page Object.
- By default, the test opens Chrome non-headless. Switch `headless=true` in config for headless runs.
