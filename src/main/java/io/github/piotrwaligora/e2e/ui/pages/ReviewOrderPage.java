package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Checkout, step 3: order summary before placing the order. */
public class ReviewOrderPage extends BasePage {

    // iOS: the total has no identifier; to be taken from the page source in CI
    private static final Map<Platform, By> TOTAL = Map.of(
            ANDROID, AppiumBy.id("totalAmountTV"));

    private static final Map<Platform, By> PLACE_ORDER_BUTTON = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'Place Order'"),
            ANDROID, AppiumBy.androidUIAutomator("new UiSelector().text(\"Place Order\")"));

    public ReviewOrderPage(AppiumDriver driver) {
        super(driver);
    }

    public ReviewOrderPage waitUntilLoaded() {
        actions.waitFor(PLACE_ORDER_BUTTON);
        return this;
    }

    public String getTotal() {
        return actions.getText(TOTAL);
    }

    public CheckoutCompletePage placeOrder() {
        actions.clickOn(PLACE_ORDER_BUTTON);
        return new CheckoutCompletePage(driver);
    }
}
