package io.github.piotrwaligora.e2e;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.AppiumServer;
import io.github.piotrwaligora.e2e.driver.DriverFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * The Appium server runs once for the whole suite; each test gets its own Appium session
 * (the app is installed and started) that is closed after the test.
 * The start and the result of every test are written to the test log.
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
    public void startSession(Method method) {
        Log.info("===== STARTED " + testName(method) + ": " + method.getAnnotation(Test.class).description());
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeSession(ITestResult result) {
        Log.info("===== " + status(result) + " " + testName(result.getMethod().getConstructorOrMethod().getMethod())
                + " (" + (result.getEndMillis() - result.getStartMillis()) / 1000 + " s)");
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static String testName(Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    private static String status(ITestResult result) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> "SKIPPED";
            default -> "FINISHED";
        };
    }
}
