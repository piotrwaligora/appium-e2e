package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** App menu: the side drawer on Android, the "More" tab on iOS. */
public class MenuPage extends BasePage {

    private static final Map<Platform, By> LOG_IN = Map.of(
            IOS, AppiumBy.accessibilityId("Login Button"),
            ANDROID, AppiumBy.accessibilityId("Login Menu Item"));

    private static final Map<Platform, By> LOG_OUT = Map.of(
            IOS, AppiumBy.accessibilityId("LogOut-menu-item"),
            ANDROID, AppiumBy.accessibilityId("Logout Menu Item"));

    public MenuPage(AppiumDriver driver) {
        super(driver);
    }

    public LoginPage openLogin() {
        actions.clickOn(LOG_IN);
        return new LoginPage(driver).waitUntilLoaded();
    }

    /** The menu offers "Log Out" only to a logged-in user. */
    public boolean isLogOutAvailable() {
        return actions.waitFor(LOG_OUT).isDisplayed();
    }
}
