package io.github.piotrwaligora.e2e.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.piotrwaligora.e2e.config.TestConfig;
import org.openqa.selenium.json.Json;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates an Appium session for the configured platform on the server managed by {@link AppiumServer}.
 * Capabilities come from {@code capabilities/<platform>.json} on the classpath, in the same format
 * as in the Appium Inspector. A relative {@code appium:app} path is resolved against the project directory.
 * <p>
 * Android: {@code appium:avd} makes Appium boot that emulator when no device is running;
 * the app opens a splash screen first, hence the wildcard in {@code appium:appWaitActivity}.
 */
public final class DriverFactory {

    private static final String APP = "appium:app";

    private DriverFactory() {
    }

    public static AppiumDriver createDriver() {
        return switch (TestConfig.platform()) {
            case ANDROID -> new AndroidDriver(AppiumServer.url(), new UiAutomator2Options(capabilities("android")));
            case IOS -> throw new UnsupportedOperationException("iOS support comes in roadmap point 7");
        };
    }

    private static Map<String, Object> capabilities(String platform) {
        String file = "capabilities/" + platform + ".json";
        Map<String, Object> capabilities = new HashMap<>(new Json().toType(readResource(file), Json.MAP_TYPE));
        capabilities.computeIfPresent(APP, (key, value) -> resolveApp(value.toString()));
        return capabilities;
    }

    private static String resolveApp(String path) {
        Path app = Path.of(path).toAbsolutePath().normalize();
        if (!Files.exists(app)) {
            throw new IllegalStateException("App not found: " + app + ". Download it with scripts/download-apps.ps1");
        }
        return app.toString();
    }

    private static String readResource(String name) {
        try (InputStream input = DriverFactory.class.getClassLoader().getResourceAsStream(name)) {
            if (input == null) {
                throw new IllegalStateException(name + " not found on the classpath");
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + name, e);
        }
    }
}
