# appium-e2e

[![Tests](https://github.com/piotrwaligora/appium-e2e/actions/workflows/tests.yml/badge.svg)](https://github.com/piotrwaligora/appium-e2e/actions/workflows/tests.yml)
[![Allure Android](https://img.shields.io/badge/Allure-Android-green)](https://piotrwaligora.github.io/appium-e2e/android/)
[![Allure iOS](https://img.shields.io/badge/Allure-iOS-blue)](https://piotrwaligora.github.io/appium-e2e/ios/)

Java and Appium based test automation framework for mobile apps: one set of tests and page objects
for Android and iOS. The app under test is the [Sauce Labs My Demo App](docs/app-analysis.md),
a demo shop with a catalog, cart, login and checkout.

Status: Android runs locally and in CI. iOS (phase 2) is next: written without a Mac and verified in CI on macOS.

## Stack

Java 25, Maven, TestNG, AssertJ, Allure, Appium 4 (java-client 11, UiAutomator2), GitHub Actions.

## Tests

| Test | Scenario |
|---|---|
| `SmokeTest` | the app starts and shows the catalog |
| `ProductDetailsTest` | a product opened from the catalog shows its name and price |
| `CartTest` | adding a product (header count, cart content) and removing it (empty cart) |
| `LoginTest` | login from the menu; login without a username is rejected (Android: error under the field, iOS: alert) |
| `CheckoutTest` | a guest adds a product, logs in at checkout, enters address and card, checks the total and places the order |

## How it works

- **Appium from code:** `BaseTest` starts the Appium server before the suite and stops it at the end (`AppiumServer`).
  A server that is already running, e.g. for the Inspector, is reused and left running.
- **Emulator:** Appium boots the emulator from the `appium:avd` capability when none is running (cold boot);
  after the suite it is stopped with `adb emu kill`, so every run starts on a fresh emulator.
- **One session per test:** each test gets a new Appium session with a clean app (empty cart, logged out).
- **A locator per platform:** each element is a `Map<Platform, By>`; `ElementActions` picks the locator of the
  platform the tests run on and waits for the element (FluentWait with polling, no `Thread.sleep`, no implicit wait):

  ```java
  private static final Map<Platform, By> TITLE = Map.of(
          IOS, AppiumBy.accessibilityId("title"),
          ANDROID, AppiumBy.id("productTV"));

  public String getTitle() {
      return actions.getText(TITLE);
  }
  ```

- **Platform differences in behaviour** stay in the page object, e.g. `LoginPage.getUsernameError()` reads
  the error text on Android and the alert on iOS.
- **Log:** every test with its progress and the elements it waited for, in the console and as Allure steps:

  ```
  22:19:51 ===== STARTED [1/7] SmokeTest.shouldShowCatalogAfterStart: App starts and shows the product catalog
  22:21:14       Waiting for: [ANDROID] AppiumBy.id: productTV -> ok
  22:21:14 ===== PASSED [1/7] SmokeTest.shouldShowCatalogAfterStart (4 s)
  ```

- **Failures:** a failed test gets a screenshot and the page source (XML of the screen) in the Allure report;
  `retry.count=1` retries it once (used in CI).

## Run locally

Prerequisites: JDK 25, Maven, Node.js with Appium 4 and the UiAutomator2 driver, Android SDK with an emulator.
See [docs/setup-windows.md](docs/setup-windows.md).

```powershell
.\scripts\download-apps.ps1
mvn test
```

No manual steps are needed before the run: the tests start Appium, and Appium boots the emulator.

Useful options:
- `mvn test -Dtest=CartTest`: one test class;
- `mvn test -Dretry.count=1`: retry a failed test once;
- `mvn test -Dandroid.emulator.shutdown=false`: keep the emulator running after the tests.

Report: `mvn allure:serve` opens the Allure report; the Appium server log is in `target/appium.log`.

Configuration:
- `src/main/resources/config.properties`: platform, Appium server, waits, retry; every key can be overridden with `-Dkey=value`;
- `src/main/resources/capabilities/<platform>.json`: session capabilities, in the same format as in the Appium Inspector.

## CI

[GitHub Actions](.github/workflows/tests.yml) runs the tests on every push and pull request to `main`, daily and on demand:
an Android emulator (API 36) on an Ubuntu runner, Appium 4, one retry for failed tests.
The Allure reports of `main` are published to GitHub Pages, one per platform:
[Android](https://piotrwaligora.github.io/appium-e2e/android/) and [iOS](https://piotrwaligora.github.io/appium-e2e/ios/);
every run keeps the report, logs, screenshots and page sources of failures as artifacts.

## Project structure

```
src/main/java/io/github/piotrwaligora/e2e/
  config/        TestConfig (config.properties + -D overrides)
  driver/        AppiumServer, DriverFactory, Emulator, Platform
  ui/            ElementActions (waits and actions)
  ui/pages/      page objects
  data/          test data
  Log            console log and Allure steps
src/test/java/io/github/piotrwaligora/e2e/
  BaseTest, *Test, listeners/ (retry)
docs/            app analysis, Windows setup
scripts/         download of the app under test
```
