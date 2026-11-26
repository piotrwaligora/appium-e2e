package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Confirmation shown after the order is placed. */
public class CheckoutCompletePage extends BasePage {

    private static final Map<Platform, By> TITLE = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND label == 'Checkout Complete'"),
            ANDROID, AppiumBy.id("completeTV"));

    public CheckoutCompletePage(AppiumDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return actions.getText(TITLE);
    }
}
