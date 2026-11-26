package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Details of one product, opened from the catalog. */
public class ProductDetailsPage extends BasePage {

    // iOS: the name label has no identifier, so it is found by the product name prefix (to be confirmed in CI)
    private static final Map<Platform, By> NAME = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText'"
                    + " AND (label BEGINSWITH 'Sauce Labs' OR label BEGINSWITH 'Test.allTheThings()')"),
            ANDROID, AppiumBy.id("productTV"));

    private static final Map<Platform, By> PRICE = Map.of(
            IOS, AppiumBy.accessibilityId("Price"),
            ANDROID, AppiumBy.id("priceTV"));

    private static final Map<Platform, By> ADD_TO_CART_BUTTON = Map.of(
            IOS, AppiumBy.accessibilityId("AddToCart"),
            ANDROID, AppiumBy.id("cartBt"));

    public ProductDetailsPage(AppiumDriver driver) {
        super(driver);
    }

    /** Waits for the "Add to cart" button: the name and price ids are also used in the catalog. */
    public ProductDetailsPage waitUntilLoaded() {
        actions.waitFor(ADD_TO_CART_BUTTON);
        return this;
    }

    public String getName() {
        return actions.getText(NAME);
    }

    public String getPrice() {
        return actions.getText(PRICE);
    }

    public ProductDetailsPage addToCart() {
        actions.clickOn(ADD_TO_CART_BUTTON);
        return this;
    }
}
