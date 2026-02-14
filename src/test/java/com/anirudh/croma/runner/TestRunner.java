
package com.anirudh.croma.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.anirudh.croma.steps"},
        plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
        monochrome = true,
        publish = false,
        tags = "@smoke"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
