package io.github.piotrwaligora.e2e;

import io.appium.java_client.AppiumBy;
import io.github.piotrwaligora.e2e.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SmokeTest extends BaseTest {

    // Temporary Android locator; platform-specific locators and page objects come in roadmap point 3
    private static final By CATALOG_TITLE = AppiumBy.id("com.saucelabs.mydemoapp.android:id/productTV");

    @Test(description = "App starts and shows the product catalog")
    public void shouldShowCatalogAfterStart() {
        String title = new WebDriverWait(driver, TestConfig.timeout(), TestConfig.polling())
                .until(ExpectedConditions.visibilityOfElementLocated(CATALOG_TITLE))
                .getText();

        assertThat(title).as("catalog title").isEqualTo("Products");
    }
}
