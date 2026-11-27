package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Shopping cart. */
public class CartPage extends BasePage {

    private static final Map<Platform, By> REMOVE_BUTTON = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'Fake Remove Item'"),
            ANDROID, AppiumBy.id("fakeRemoveBt"));

    private static final Map<Platform, By> EMPTY_CART_TITLE = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == 'No Items'"),
            ANDROID, AppiumBy.id("noItemTitleTV"));

    private static final Map<Platform, By> PROCEED_TO_CHECKOUT = Map.of(
            IOS, AppiumBy.accessibilityId("ProceedToCheckout"),
            ANDROID, AppiumBy.androidUIAutomator("new UiSelector().text(\"Proceed To Checkout\")"));

    public CartPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean containsProduct(String name) {
        return actions.waitFor(productByName(name)).isDisplayed();
    }

    /** Removes the only product in the cart. */
    public CartPage removeProduct() {
        actions.clickOn(REMOVE_BUTTON);
        return this;
    }

    public String getEmptyCartTitle() {
        return actions.getText(EMPTY_CART_TITLE);
    }

    /** A guest is asked to log in first. */
    public LoginPage proceedToCheckoutAsGuest() {
        actions.clickOn(PROCEED_TO_CHECKOUT);
        return new LoginPage(driver).waitUntilLoaded();
    }

    private static Map<Platform, By> productByName(String name) {
        return Map.of(
                // iOS product names have the color appended ("Sauce Labs Backpack - Black")
                IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label BEGINSWITH '" + name + "'"),
                ANDROID, AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*:id/titleTV\").text(\"" + name + "\")"));
    }
}
