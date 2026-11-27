package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.config.TestConfig;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;

import java.util.Map;

import static io.github.piotrwaligora.e2e.driver.Platform.ANDROID;
import static io.github.piotrwaligora.e2e.driver.Platform.IOS;

/**
 * Login form, opened from the menu or when a guest proceeds to checkout.
 * Both apps list test users under the form; tapping one fills in the user name and the password.
 * The tests use that list instead of typing: the iOS app cannot hide the on-screen keyboard,
 * which then covers the Login button.
 */
public class LoginPage extends BasePage {

    /** First user of the list: Android "bod@example.com", iOS "bob@example.com" (password 10203040). */
    private static final Map<Platform, By> FIRST_LISTED_USER = Map.of(
            IOS, AppiumBy.accessibilityId("bob@example.com"),
            ANDROID, AppiumBy.id("username1TV"));

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

    /** Logs in as the first listed user; the next screen depends on where the login was opened from. */
    public void loginAsListedUser() {
        actions.clickOn(FIRST_LISTED_USER);
        submit();
    }

    public LoginPage submit() {
        actions.clickOn(LOGIN_BUTTON);
        return this;
    }

    /** Android shows the error under the field, iOS in an alert. */
    public String getUsernameError() {
        if (TestConfig.platform() == IOS) {
            return actions.getAlertText();
        }
        return actions.getText(USERNAME_ERROR);
    }
}
