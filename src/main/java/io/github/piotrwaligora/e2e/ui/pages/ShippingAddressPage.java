package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.data.TestData.Address;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Checkout, step 1: shipping address. iOS fields are found by their placeholder (no identifiers). */
public class ShippingAddressPage extends BasePage {

    private static final Map<Platform, By> FULL_NAME = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == 'Rebecca Winter'"),
            ANDROID, AppiumBy.id("fullNameET"));

    private static final Map<Platform, By> ADDRESS_LINE = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == 'Mandorley 112'"),
            ANDROID, AppiumBy.id("address1ET"));

    private static final Map<Platform, By> CITY = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == 'Truro'"),
            ANDROID, AppiumBy.id("cityET"));

    private static final Map<Platform, By> ZIP_CODE = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == '89750'"),
            ANDROID, AppiumBy.id("zipET"));

    private static final Map<Platform, By> COUNTRY = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == 'United Kingdom'"),
            ANDROID, AppiumBy.id("countryET"));

    private static final Map<Platform, By> TO_PAYMENT_BUTTON = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'To Payment'"),
            ANDROID, AppiumBy.androidUIAutomator("new UiSelector().text(\"To Payment\")"));

    public ShippingAddressPage(AppiumDriver driver) {
        super(driver);
    }

    public ShippingAddressPage waitUntilLoaded() {
        actions.waitFor(TO_PAYMENT_BUTTON);
        return this;
    }

    public PaymentPage enterAddress(Address address) {
        actions.type(FULL_NAME, address.fullName());
        actions.type(ADDRESS_LINE, address.addressLine());
        actions.type(CITY, address.city());
        actions.type(ZIP_CODE, address.zipCode());
        actions.type(COUNTRY, address.country());
        actions.clickOn(TO_PAYMENT_BUTTON);
        return new PaymentPage(driver).waitUntilLoaded();
    }
}
