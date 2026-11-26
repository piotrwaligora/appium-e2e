package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.config.TestConfig;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/** Login form, opened from the menu or when a guest proceeds to checkout. */
public class LoginPage extends BasePage {

    // iOS: the login fields have no identifiers; to be confirmed in the page source in CI
    private static final Map<Platform, By> USERNAME = Map.of(
            IOS, AppiumBy.iOSClassChain("**/XCUIElementTypeTextField"),
            ANDROID, AppiumBy.id("nameET"));

    private static final Map<Platform, By> PASSWORD = Map.of(
            IOS, AppiumBy.iOSClassChain("**/XCUIElementTypeSecureTextField"),
            ANDROID, AppiumBy.id("passwordET"));

    private static final Map<Platform, By> LOGIN_BUTTON = Map.of(
            IOS, AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'Login'"),
            ANDROID, AppiumBy.id("loginBtn"));

    private static final Map<Platform, By> USERNAME_ERROR = Map.of(
            ANDROID, AppiumBy.id("nameErrorTV"));

    public LoginPage(AppiumDriver driver) {
        super(driver);
    }

    public LoginPage waitUntilLoaded() {
        actions.waitFor(LOGIN_BUTTON);
        return this;
    }

    /** Fills in the form and submits it; the next screen depends on where the login was opened from. */
    public void login(String username, String password) {
        if (!username.isEmpty()) {
            actions.type(USERNAME, username);
        }
        actions.type(PASSWORD, password);
        actions.clickOn(LOGIN_BUTTON);
    }

    /** Android shows the error under the field, iOS in an alert. */
    public String getUsernameError() {
        if (TestConfig.platform() == IOS) {
            return actions.getAlertText();
        }
        return actions.getText(USERNAME_ERROR);
    }
}
