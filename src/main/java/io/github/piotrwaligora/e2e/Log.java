package io.github.piotrwaligora.e2e;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Test log printed to standard output, so it is visible in the Maven console, the IDE and CI
 * without a logging library, e.g. {@code 15:20:12 ===== STARTED [1/7] SmokeTest.shouldShowCatalog...}.
 */
public final class Log {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Log() {
    }

    public static void info(String message) {
        System.out.println(LocalTime.now().format(TIME) + " " + message);
    }

    /** A step inside a test: indented in the console and added as a step to the Allure report. */
    public static void step(String message) {
        info("      " + message); // aligned with the text after "===== "
        Allure.step(message);
    }

    /** A step inside a test that failed: logged like {@link #step} and marked as failed in the Allure report. */
    public static void failedStep(String message) {
        info("      " + message);
        Allure.step(message, Status.FAILED);
    }
}
