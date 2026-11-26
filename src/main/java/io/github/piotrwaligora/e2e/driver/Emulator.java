package io.github.piotrwaligora.e2e.driver;

import io.github.piotrwaligora.e2e.Log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Stops the Android emulator through adb. {@code adb emu kill} shuts it down cleanly (unlike killing
 * the process), so the next run boots a fresh emulator without lock files left behind.
 */
public final class Emulator {

    private static final Duration STOP_TIMEOUT = Duration.ofSeconds(60);

    private Emulator() {
    }

    public static void stop() {
        if (!adb("devices").contains("emulator-")) {
            return;
        }
        Log.info("[Emulator] Stopping the Android emulator");
        adb("emu", "kill");
        adb("wait-for-disconnect");
    }

    /** Android SDK from ANDROID_HOME; the IDE may not have the variable, then the default location. */
    public static Path androidHome() {
        String androidHome = System.getenv("ANDROID_HOME");
        return androidHome != null ? Path.of(androidHome) : Path.of(System.getProperty("user.home"), "Android", "Sdk");
    }

    /** Runs an adb command and returns its output. */
    private static String adb(String... args) {
        List<String> command = new ArrayList<>();
        command.add(androidHome().resolve("platform-tools").resolve("adb").toString());
        command.addAll(List.of(args));
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            if (!process.waitFor(STOP_TIMEOUT.toSeconds(), TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("adb " + String.join(" ", args) + " did not finish in " + STOP_TIMEOUT);
            }
            return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        }
    }
}
