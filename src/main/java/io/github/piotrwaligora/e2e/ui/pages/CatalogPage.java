package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Product catalog, the first screen after the app starts. */
public class CatalogPage extends BasePage {

    private static final Map<Platform, By> TITLE = Map.of(
            IOS, AppiumBy.accessibilityId("title"),
            ANDROID, AppiumBy.id("productTV"));

    public CatalogPage(AppiumDriver driver) {
        super(driver);
    }

    public CatalogPage waitUntilLoaded() {
        actions.waitFor(TITLE);
        return this;
    }

    public String getTitle() {
        return actions.getText(TITLE);
    }

    /** Opens a product shown on the first screen of the catalog (scrolling comes with the scenarios that need it). */
    public ProductDetailsPage openProduct(String name) {
        actions.clickOn(productByName(name));
        return new ProductDetailsPage(driver).waitUntilLoaded();
    }

    /** Android: only the product image opens the details, so the image next to the name is used. */
    private static Map<Platform, By> productByName(String name) {
        return Map.of(
                IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == '" + name + "'"),
                ANDROID, AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*:id/titleTV\")"
                        + ".text(\"" + name + "\").fromParent(new UiSelector().resourceIdMatches(\".*:id/productIV\"))"));
    }
}
