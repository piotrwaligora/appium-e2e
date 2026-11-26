package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.data.TestData.PaymentCard;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Checkout, step 2: payment card. iOS fields are found by their placeholder (no identifiers). */
public class PaymentPage extends BasePage {

    private static final Map<Platform, By> CARD_HOLDER = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == 'Maxim Winter'"),
            ANDROID, AppiumBy.id("nameET"));

    private static final Map<Platform, By> CARD_NUMBER = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == '3258 1265 7568 7896'"),
            ANDROID, AppiumBy.id("cardNumberET"));

    private static final Map<Platform, By> EXPIRATION_DATE = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == '03/25'"),
            ANDROID, AppiumBy.id("expirationDateET"));

    private static final Map<Platform, By> SECURITY_CODE = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("placeholderValue == '123'"),
            ANDROID, AppiumBy.id("securityCodeET"));

    private static final Map<Platform, By> REVIEW_ORDER_BUTTON = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'Review Order'"),
            ANDROID, AppiumBy.androidUIAutomator("new UiSelector().text(\"Review Order\")"));

    public PaymentPage(AppiumDriver driver) {
        super(driver);
    }

    public PaymentPage waitUntilLoaded() {
        actions.waitFor(REVIEW_ORDER_BUTTON);
        return this;
    }

    /** Expiration date as typed on the keypad, e.g. "0330" (the app adds the slash). */
    public ReviewOrderPage enterCard(PaymentCard card) {
        actions.type(CARD_HOLDER, card.holder());
        actions.type(CARD_NUMBER, card.number());
        actions.type(EXPIRATION_DATE, card.expiration());
        actions.type(SECURITY_CODE, card.securityCode());
        actions.clickOn(REVIEW_ORDER_BUTTON);
        return new ReviewOrderPage(driver).waitUntilLoaded();
    }
}
