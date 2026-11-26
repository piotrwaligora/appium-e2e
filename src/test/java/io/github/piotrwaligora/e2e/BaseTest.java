package io.github.piotrwaligora.e2e;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.driver.AppiumServer;
import io.github.piotrwaligora.e2e.driver.DriverFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Appium server runs once for the whole suite; each test gets its own Appium session
 * (the app is installed and started) that is closed after the test.
 * The start and the result of every test are written to the test log.
 */
public abstract class BaseTest {

    /** Number of the test in the run, for the progress in the log: [1/10], [2/10], ... */
    private static final AtomicInteger TEST_COUNTER = new AtomicInteger();

    protected AppiumDriver driver;
    private String progress;

    @BeforeSuite
    public void startAppiumServer() {
        AppiumServer.start();
    }

    @AfterSuite(alwaysRun = true)
    public void stopAppiumServer() {
        AppiumServer.stop();
    }

    @BeforeMethod
    public void startSession(Method method, ITestContext context) {
        progress = "[" + TEST_COUNTER.incrementAndGet() + "/" + context.getAllTestMethods().length + "]";
        Log.info("===== STARTED " + progress + " " + testName(method) + ": " + method.getAnnotation(Test.class).description());
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeSession(ITestResult result) {
        Log.info("===== " + status(result) + " " + progress + " " + testName(result.getMethod().getConstructorOrMethod().getMethod())
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
