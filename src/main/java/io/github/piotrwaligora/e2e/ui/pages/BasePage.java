package io.github.piotrwaligora.e2e.ui.pages;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.ui.ElementActions;

/** Base of all page objects: the driver and element actions with waits. */
public abstract class BasePage {

    protected final AppiumDriver driver;
    protected final ElementActions actions;

    protected BasePage(AppiumDriver driver) {
        this.driver = driver;
        this.actions = new ElementActions(driver);
    }
}
