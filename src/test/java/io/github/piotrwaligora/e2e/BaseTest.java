package io.github.piotrwaligora.e2e;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.AppiumServer;
import io.github.piotrwaligora.e2e.driver.DriverFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * The Appium server runs once for the whole suite; each test gets its own Appium session
 * (the app is installed and started) that is closed after the test.
 */
public abstract class BaseTest {

    protected AppiumDriver driver;

    @BeforeSuite
    public void startAppiumServer() {
        AppiumServer.start();
    }

    @AfterSuite(alwaysRun = true)
    public void stopAppiumServer() {
        AppiumServer.stop();
    }

    @BeforeMethod
    public void startSession() {
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeSession() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
