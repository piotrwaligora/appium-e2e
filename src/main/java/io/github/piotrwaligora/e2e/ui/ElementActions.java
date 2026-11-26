package io.github.piotrwaligora.e2e.ui;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.config.TestConfig;
import io.github.piotrwaligora.e2e.driver.Platform;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        return wait.withMessage("element " + by).until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /** Waits until the element is visible and enabled. */
    public WebElement waitForClickable(Map<Platform, By> locator) {
        By by = findBy(locator);
        return wait.withMessage("clickable element " + by).until(ExpectedConditions.elementToBeClickable(by));
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

    /** Locator of the platform the tests run on. */
    private By findBy(Map<Platform, By> locator) {
        Platform platform = TestConfig.platform();
        By by = locator.get(platform);
        if (by == null) {
            throw new IllegalStateException("No " + platform + " locator in " + locator);
        }
        return by;
    }
}
