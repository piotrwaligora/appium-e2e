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

    // iOS: the total has no identifier. Product prices are "$ 29.99" (with a space), delivery "$5.99",
    // the total "$35.98" (to be confirmed in CI)
    private static final Map<Platform, By> TOTAL = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText'"
                    + " AND label BEGINSWITH '$' AND NOT label BEGINSWITH '$ ' AND label != '$5.99'"),
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

    /** Total without spaces, e.g. "$35.98": Android shows "$ 35.98", iOS "$35.98". */
    public String getTotal() {
        return actions.getText(TOTAL).replace(" ", "");
    }

    public CheckoutCompletePage placeOrder() {
        actions.clickOn(PLACE_ORDER_BUTTON);
        return new CheckoutCompletePage(driver);
    }
}
