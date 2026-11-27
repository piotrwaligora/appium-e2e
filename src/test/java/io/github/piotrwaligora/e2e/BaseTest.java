package io.github.piotrwaligora.e2e;

import io.appium.java_client.AppiumDriver;
import io.github.piotrwaligora.e2e.config.TestConfig;
import io.github.piotrwaligora.e2e.driver.AppiumServer;
import io.github.piotrwaligora.e2e.driver.DriverFactory;
import io.github.piotrwaligora.e2e.driver.Emulator;
import io.github.piotrwaligora.e2e.driver.Platform;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Appium server runs once for the whole suite; each test gets its own Appium session
 * (the app is installed and started) that is closed after the test. After the suite the Android
 * emulator is stopped as well ({@code android.emulator.shutdown}), so the next run boots a fresh one.
 * The start and the result of every test are written to the test log; a failed test gets
 * a screenshot and the page source in the Allure report.
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
    public void stopAppiumServerAndEmulator() {
        AppiumServer.stop();
        if (TestConfig.platform() == Platform.ANDROID && TestConfig.androidEmulatorShutdown()) {
            Emulator.stop();
        }
    }

    @BeforeMethod
    public void startSession(Method method, ITestContext context) {
        progress = "[" + TEST_COUNTER.incrementAndGet() + "/" + context.getAllTestMethods().length + "]";
        Log.info("===== STARTED " + progress + " " + testName(method) + ": " + method.getAnnotation(Test.class).description());
        // Tells Android and iOS results of the same test apart in the combined Allure report
        Allure.parameter("platform", TestConfig.platform());
        skipOnOtherPlatforms(method);
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeSession(ITestResult result) {
        Log.info("===== " + status(result) + " " + progress + " " + testName(result.getMethod().getConstructorOrMethod().getMethod())
                + " (" + (result.getEndMillis() - result.getStartMillis()) / 1000 + " s)");
        if (result.wasRetried()) {
            TEST_COUNTER.decrementAndGet(); // the retry runs under the same number
        }
        if (driver == null) {
            return;
        }
        try {
            if (!result.isSuccess()) {
                attachAppState();
            }
        } finally {
            driver.quit();
            driver = null;
        }
    }

    /** Screenshot and page source (XML of the screen) in the Allure report, to find out why a test failed. */
    private void attachAppState() {
        byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
        Allure.attachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot),
                AttachmentOptions.withFileExtension(".png"));
        Allure.attachment("Page source on failure", "text/xml",
                new ByteArrayInputStream(driver.getPageSource().getBytes(StandardCharsets.UTF_8)),
                AttachmentOptions.withFileExtension(".xml"));
    }

    /**
     * A test without {@link Platforms} runs everywhere; with it, only on the listed platforms.
     * configfailurepolicy=continue keeps the skip from skipping the next tests of the class.
     */
    private static void skipOnOtherPlatforms(Method method) {
        Platforms platforms = method.getAnnotation(Platforms.class);
        if (platforms != null && !List.of(platforms.value()).contains(TestConfig.platform())) {
            throw new SkipException("Not for " + TestConfig.platform() + ": " + platforms.reason());
        }
    }

    private static String testName(Method method) {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    private static String status(ITestResult result) {
        return switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED";
            case ITestResult.FAILURE -> "FAILED";
            case ITestResult.SKIP -> result.wasRetried() ? "RETRY" : "SKIPPED";
            default -> "FINISHED";
        };
    }
}
