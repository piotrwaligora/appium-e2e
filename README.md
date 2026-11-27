# appium-e2e

[![Tests](https://github.com/piotrwaligora/appium-e2e/actions/workflows/tests.yml/badge.svg)](https://github.com/piotrwaligora/appium-e2e/actions/workflows/tests.yml)
[![Allure Android](https://img.shields.io/badge/Allure-Android-green)](https://piotrwaligora.github.io/appium-e2e/android/)
[![Allure iOS](https://img.shields.io/badge/Allure-iOS-blue)](https://piotrwaligora.github.io/appium-e2e/ios/)

Java and Appium based test automation framework for mobile apps: one set of tests and page objects
for Android and iOS. The app under test is the [Sauce Labs My Demo App](docs/app-analysis.md),
a demo shop with a catalog, cart, login and checkout.

Both platforms run in CI on every push and pull request: Android on an emulator, iOS on a simulator.
The iOS part was written without a Mac and brought to green from the CI page sources and screenshots.

## Stack

Java 25, Maven, TestNG, AssertJ, Allure, Appium 4 (java-client 11, UiAutomator2, XCUITest), GitHub Actions.

## Tests

| Test | Scenario | Android | iOS |
|---|---|:---:|:---:|
| `SmokeTest` | the app starts and the catalog shows a product | ✅ | ✅ |
| `ProductDetailsTest` | a product opened from the catalog shows its name and price | ✅ | ✅ |
| `CartTest` | adding a product (header count, cart content) and removing it (empty cart) | ✅ | ✅ |
| `LoginTest` | login from the menu; a login with an empty form is rejected (username required) | ✅ | ✅ |
| `CheckoutTest` | a guest adds a product, logs in at checkout, enters address and card, checks the total and places the order | ✅ | ⏭️ [known issue](#known-issue-ios-checkout) |

## How it works

- **Appium from code:** `BaseTest` starts the Appium server before the suite and stops it at the end (`AppiumServer`).
  A server that is already running, e.g. for the Inspector, is reused and left running.
- **Emulator:** Appium boots the Android emulator from the `appium:avd` capability when none is running (cold boot);
  after the suite it is stopped with `adb emu kill`, so every run starts on a fresh emulator.
- **One session per test:** each test gets a new Appium session with a clean app (empty cart, logged out).
- **A locator per platform:** each element is a `Map<Platform, By>`; `ElementActions` picks the locator of the
  platform the tests run on and waits for the element (FluentWait with polling, no `Thread.sleep`, no implicit wait):

  ```java
  private static final Map<Platform, By> TITLE = Map.of(
          IOS, AppiumBy.accessibilityId("title"),
          ANDROID, AppiumBy.id("productTV"));

  public CatalogPage waitUntilLoaded() {
      actions.waitFor(TITLE);
      return this;
  }
  ```

- **Platform differences in behaviour** stay in the page object, e.g. `LoginPage.getUsernameError()` reads
  the error text on Android and the alert on iOS.
- **Tests for some platforms only:** a test without an annotation runs everywhere; `@Platforms(ANDROID)` runs it on
  Android only and skips it on iOS with a reason, shown in the log and the report:

  ```java
  @Platforms(value = ANDROID, reason = "iOS app issue: the on-screen keyboard cannot be dismissed ...")
  @Test(description = "Guest adds a product, logs in at checkout and places the order")
  public void shouldPlaceOrder() { ... }
  ```

- **Log:** every test with its progress and the elements it waited for, in the console and as Allure steps:

  ```
  22:19:51 ===== STARTED [1/7] SmokeTest.shouldShowCatalogAfterStart: App starts and shows the product catalog
  22:21:14       Waiting for: [ANDROID] AppiumBy.id: productTV -> ok
  22:21:14 ===== PASSED [1/7] SmokeTest.shouldShowCatalogAfterStart (4 s)
  ```

  A wait that times out is logged as `Waiting for: ... -> not found` and marked as a failed step in the report.
- **Failures:** a failed test gets a screenshot and the page source (XML of the screen) in the Allure report;
  `retry.count=1` retries it once (used in CI). A session that does not start is tried once more; if it fails
  again, the test fails with the reason (it is not skipped), so the retry covers it too.

## Platform differences handled in the page objects

Found in the CI runs of the iOS app (see [docs/app-analysis.md](docs/app-analysis.md#findings-from-the-ci-runs)):

| Difference | Android | iOS | Handled by |
|---|---|---|---|
| Product names | "Sauce Labs Backpack" | "Sauce Labs Backpack - Black" | locators and checks use the name as a prefix |
| Catalog title | text "Products" | accessibility label `title` | the smoke test checks a product in the catalog |
| Cart count | visible label | label covered by the cart tab button | read when present, not when visible |
| Order total | "$ 35.98" | "$35.98" | `ReviewOrderPage.getTotal()` removes spaces |
| Validation errors | text under the field | alert | `LoginPage.getUsernameError()` |
| On-screen keyboard | does not cover the buttons | cannot be dismissed, covers the buttons below the form | login by tapping a listed test user; checkout Android-only |

## Known issue: iOS checkout

After typing in the iOS shipping address form, the on-screen keyboard stays open and covers the fixed
"To Payment" button below the form. The app has no way to dismiss it (no return-key handling, no dismiss on drag
or tap, `hideKeyboard` fails), and a hardware keyboard on the CI simulator did not hide it either.
A user on an iPhone would get stuck in the same place, so this is an issue of the app, not of the tests.
`CheckoutTest` is therefore marked `@Platforms(ANDROID)` and skipped on iOS.

## Run locally

Prerequisites: JDK 25, Maven, Node.js with Appium 4 and the UiAutomator2 driver, Android SDK with an emulator.
See [docs/setup-windows.md](docs/setup-windows.md).

```powershell
.\scripts\download-apps.ps1 android
mvn test
```

No manual steps are needed before the run: the tests start Appium, and Appium boots the emulator.

Useful options:
- `mvn test -Dtest=CartTest`: one test class;
- `mvn test -Dretry.count=1`: retry a failed test once;
- `mvn test -Dandroid.emulator.shutdown=false`: keep the emulator running after the tests;
- `mvn test -Dplatform=ios`: iOS (macOS with Xcode and the XCUITest driver only, as in CI).

Report: `mvn allure:serve` opens the Allure report; the Appium server log is in `target/appium.log`.

Configuration:
- `src/main/resources/config.properties`: platform, Appium server, waits, retry; every key can be overridden with `-Dkey=value`;
- `src/main/resources/capabilities/<platform>.json`: session capabilities, in the same format as in the Appium Inspector;
  any capability can be overridden with `-Dappium:<name>=...`, e.g. `-Dappium:udid=...`.

## CI

[GitHub Actions](.github/workflows/tests.yml) runs the tests on every push and pull request to `main`, daily and on demand.
The two platform jobs run in parallel, each with one retry for failed tests:

- **Android:** Ubuntu runner with KVM, emulator API 36 (`android-emulator-runner`), Appium 4 + UiAutomator2;
- **iOS:** macOS runner, an iPhone simulator with the newest iOS on the runner, booted before the tests and passed
  with `-Dappium:udid`; Appium 4 + XCUITest, WebDriverAgent built before the tests.

A report job then builds one Allure report per platform. The reports of `main` are published to GitHub Pages:
[Android](https://piotrwaligora.github.io/appium-e2e/android/) and [iOS](https://piotrwaligora.github.io/appium-e2e/ios/);
every run keeps the reports, logs, screenshots and page sources of failures as artifacts.

## Project structure

```
src/main/java/io/github/piotrwaligora/e2e/
  config/        TestConfig (config.properties + -D overrides)
  driver/        AppiumServer, DriverFactory, Emulator, Platform
  ui/            ElementActions (waits and actions)
  ui/pages/      page objects
  data/          test data
  Log            console log and Allure steps
src/main/resources/
  capabilities/  android.json, ios.json
src/test/java/io/github/piotrwaligora/e2e/
  BaseTest, Platforms, *Test, listeners/ (retry)
.github/         CI workflow, start page of the reports
docs/            app analysis, Windows setup
scripts/         download of the apps under test
```
