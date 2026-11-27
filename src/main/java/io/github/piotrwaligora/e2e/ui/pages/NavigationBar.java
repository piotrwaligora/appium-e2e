package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/**
 * Navigation available on every screen: the header with menu and cart icons on Android,
 * the tab bar (Catalog, Cart, More) on iOS.
 */
public class NavigationBar extends BasePage {

    private static final Map<Platform, By> MENU = Map.of(
            IOS, AppiumBy.accessibilityId("More-tab-item"),
            ANDROID, AppiumBy.accessibilityId("View menu"));

    private static final Map<Platform, By> CART = Map.of(
            IOS, AppiumBy.accessibilityId("Cart-tab-item"),
            ANDROID, AppiumBy.accessibilityId("View cart"));

    // iOS: the count label has no identifier; its name is the number, unlike the product quantity "Amount"
    private static final Map<Platform, By> CART_COUNT = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND name != 'Amount' AND label MATCHES '^[0-9]+$'"),
            ANDROID, AppiumBy.id("cartTV"));

    public NavigationBar(AppiumDriver driver) {
        super(driver);
    }

    public MenuPage openMenu() {
        actions.clickOn(MENU);
        return new MenuPage(driver);
    }

    public CartPage openCart() {
        actions.clickOn(CART);
        return new CartPage(driver);
    }

    /** iOS reports the count as not visible (the cart tab button covers it), so it is read when present. */
    public String getCartCount() {
        return actions.waitForPresent(CART_COUNT).getText();
    }
}
