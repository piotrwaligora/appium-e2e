package io.github.piotrwaligora.e2e.ui;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.Log;
import io.github.piotrwaligora.e2e.config.TestConfig;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

/**
 * Element interactions synchronized with {@link FluentWait}.
 * Page objects describe an element as a map with a locator per platform, e.g.
 * {@code Map.of(IOS, AppiumBy.accessibilityId("title"), ANDROID, AppiumBy.id("productTV"))};
 * every action picks the locator of the current platform ({@link #findBy}), locates the element again
 * and waits until it is ready. Implicit waits and {@code Thread.sleep} are intentionally not used.
 */
public class ElementActions {

    private final AppiumDriver driver;
    private final FluentWait<WebDriver> wait;

    public ElementActions(AppiumDriver driver) {
        this.driver = driver;
        // WebDriverWait is a FluentWait that already ignores NotFoundException while polling.
        this.wait = new WebDriverWait(driver, TestConfig.timeout(), TestConfig.polling())
                .ignoring(StaleElementReferenceException.class);
    }

    /** Waits until the element is present and visible. */
    public WebElement waitFor(Map<Platform, By> locator) {
        By by = findBy(locator);
        return waitUntil(wait, by, ExpectedConditions.visibilityOfElementLocated(by));
    }

    /** Waits longer (or shorter) than the default timeout, for a screen that needs it. */
    public WebElement waitFor(Map<Platform, By> locator, Duration timeout) {
        By by = findBy(locator);
        FluentWait<WebDriver> customWait = new WebDriverWait(driver, timeout, TestConfig.polling())
                .ignoring(StaleElementReferenceException.class);
        return waitUntil(customWait, by, ExpectedConditions.visibilityOfElementLocated(by));
    }

    /** Waits until the element is in the screen tree, also when another element covers it. */
    public WebElement waitForPresent(Map<Platform, By> locator) {
        By by = findBy(locator);
        return waitUntil(wait, by, ExpectedConditions.presenceOfElementLocated(by));
    }

    /** Waits until the element is visible and enabled. */
    public WebElement waitForClickable(Map<Platform, By> locator) {
        By by = findBy(locator);
        return waitUntil(wait, by, ExpectedConditions.elementToBeClickable(by));
    }

    public void clickOn(Map<Platform, By> locator) {
        waitForClickable(locator).click();
    }

    public void type(Map<Platform, By> locator, String text) {
        WebElement element = waitForClickable(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getText(Map<Platform, By> locator) {
        return waitFor(locator).getText();
    }

    /** Checks visibility immediately, without waiting. */
    public boolean isDisplayed(Map<Platform, By> locator) {
        return driver.findElements(findBy(locator)).stream().anyMatch(WebElement::isDisplayed);
    }

    /** Waits for a system alert (e.g. an iOS validation message) and returns its text. */
    public String getAlertText() {
        String text = wait.withMessage("alert").until(ExpectedConditions.alertIsPresent()).getText();
        Log.step("Waiting for: alert -> ok, text: " + text);
        return text;
    }

    /** Locator of the platform the tests run on. */
    private By findBy(Map<Platform, By> locator) {
        Platform platform = TestConfig.platform();
        By by = locator.get(platform);
        if (by == null) {
            throw new IllegalStateException("No " + platform + " locator in " + locator);
        }
        return by;
    }

    /**
     * Waits for the element and logs the result as a step, e.g.
     * {@code Waiting for: [ANDROID] AppiumBy.id: productTV -> ok} or {@code ... -> not found}.
     */
    private static WebElement waitUntil(FluentWait<WebDriver> wait, By by, ExpectedCondition<WebElement> condition) {
        try {
            WebElement element = wait.withMessage("element " + describe(by)).until(condition);
            Log.step("Waiting for: " + describe(by) + " -> ok");
            return element;
        } catch (TimeoutException e) {
            Log.failedStep("Waiting for: " + describe(by) + " -> not found");
            throw e;
        }
    }

    /** Locator with its platform for timeout messages, e.g. {@code [ANDROID] By.id: productTV}. */
    private static String describe(By by) {
        return "[" + TestConfig.platform() + "] " + by;
    }
}
