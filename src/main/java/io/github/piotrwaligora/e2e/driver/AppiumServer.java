package io.github.piotrwaligora.e2e.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.piotrwaligora.e2e.config.TestConfig;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Starts and stops the Appium server for the test run, so nobody has to start it by hand.
 * <ul>
 *     <li>Port free: a new server is started; its PID is kept in {@code target/appium.pid}.</li>
 *     <li>Port taken by a server left over from an interrupted run (PID file): it is stopped and replaced.</li>
 *     <li>Port taken by any other Appium server, e.g. one used by the Inspector: it is reused and left running.</li>
 * </ul>
 * The server log is written to {@code target/appium.log}.
 */
public final class AppiumServer {

    private static final Path PID_FILE = Path.of("target", "appium.pid");
    private static final Path LOG_FILE = Path.of("target", "appium.log");
    private static final Duration STOP_TIMEOUT = Duration.ofSeconds(15);
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    /** Set only for a server started by this class; an external server is never stopped. */
    private static AppiumDriverLocalService service;

    private AppiumServer() {
    }

    public static synchronized void start() {
        if (service != null) {
            return;
        }
        if (isRunning()) {
            if (!stopLeftoverServer()) {
                log("Using the Appium server already running at " + url());
                return;
            }
        }
        createDirectories(LOG_FILE.getParent());
        service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .withIPAddress(TestConfig.appiumHost())
                .usingPort(TestConfig.appiumPort())
                .withEnvironment(environment())
                .withLogFile(LOG_FILE.toFile()));
        service.clearOutPutStreams(); // the full log goes to the log file, not to the console
        log("Starting Appium server at " + url() + ", log: " + LOG_FILE.toAbsolutePath());
        service.start();
        savePid();
        Runtime.getRuntime().addShutdownHook(new Thread(AppiumServer::stop, "appium-server-stop"));
    }

    public static synchronized void stop() {
        if (service == null) {
            return;
        }
        log("Stopping Appium server");
        service.stop();
        service = null;
        deleteIfExists(PID_FILE);
    }

    public static boolean isRunning() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url() + "status"))
                    .timeout(Duration.ofSeconds(2))
                    .build();
            return HTTP.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** Server URL with a trailing slash, e.g. {@code http://127.0.0.1:4723/}. */
    public static URL url() {
        try {
            return URI.create("http://%s:%d/".formatted(TestConfig.appiumHost(), TestConfig.appiumPort())).toURL();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Stops a server started by an earlier run that could not stop it itself
     * (for example, the run was killed from the IDE).
     *
     * @return true if such a server was found and stopped
     */
    private static boolean stopLeftoverServer() {
        Optional<ProcessHandle> leftover = readPid()
                .flatMap(ProcessHandle::of)
                .filter(ProcessHandle::isAlive)
                .filter(process -> process.info().command().map(AppiumServer::isNode).orElse(false));
        deleteIfExists(PID_FILE);
        if (leftover.isEmpty()) {
            return false;
        }
        ProcessHandle process = leftover.get();
        log("Stopping Appium server left over from an earlier run (PID " + process.pid() + ")");
        process.descendants().forEach(ProcessHandle::destroy);
        process.destroy();
        try {
            process.onExit().get(STOP_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            throw new IllegalStateException("Appium server left over from an earlier run did not stop", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

    /** Remembers the PID of the server (the node process started by the service). */
    private static void savePid() {
        ProcessHandle.current().children()
                .filter(process -> process.info().command().map(AppiumServer::isNode).orElse(false))
                .findFirst()
                .ifPresent(process -> write(PID_FILE, String.valueOf(process.pid())));
    }

    /** The server needs the Android SDK and a JDK; fall back to the defaults when the IDE has no variables. */
    private static Map<String, String> environment() {
        Map<String, String> environment = new HashMap<>();
        if (System.getenv("ANDROID_HOME") == null) {
            environment.put("ANDROID_HOME", Path.of(System.getProperty("user.home"), "Android", "Sdk").toString());
        }
        if (System.getenv("JAVA_HOME") == null) {
            environment.put("JAVA_HOME", System.getProperty("java.home"));
        }
        return environment;
    }

    private static boolean isNode(String command) {
        String executable = Path.of(command).getFileName().toString();
        return executable.equals("node") || executable.equals("node.exe");
    }

    private static Optional<Long> readPid() {
        try {
            return Files.exists(PID_FILE) ? Optional.of(Long.parseLong(Files.readString(PID_FILE).strip())) : Optional.empty();
        } catch (IOException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static void write(Path file, String content) {
        try {
            Files.writeString(file, content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void createDirectories(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void deleteIfExists(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void log(String message) {
        System.out.println("[Appium] " + message);
    }
}
