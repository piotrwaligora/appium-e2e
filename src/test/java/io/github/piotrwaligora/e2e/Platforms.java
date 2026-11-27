package io.github.piotrwaligora.e2e;

import io.github.piotrwaligora.e2e.driver.Platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Platforms a test runs on; on the other platforms it is skipped (see {@link BaseTest}).
 * A test without this annotation runs on all platforms.
 * <pre>{@code
 * @Platforms(value = ANDROID, reason = "...")   // Android only
 * @Platforms({IOS, ANDROID})                    // all platforms, same as no annotation
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Platforms {

    Platform[] value();

    /** Why the test does not run on the other platforms; shown in the log and the report. */
    String reason() default "";
}
