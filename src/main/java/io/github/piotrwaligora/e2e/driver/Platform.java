package io.github.piotrwaligora.e2e.driver;

import java.util.Arrays;
import java.util.Locale;

/** Mobile platform the tests run on, selected with the {@code platform} property. */
public enum Platform {
    ANDROID,
    IOS;

    public static Platform from(String value) {
        return Arrays.stream(values())
                .filter(platform -> platform.name().equals(value.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported platform: " + value + ", expected one of " + Arrays.toString(values())));
    }
}
