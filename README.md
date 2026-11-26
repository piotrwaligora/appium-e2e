# appium-e2e

Java and Appium based test automation framework for mobile apps: one set of tests for Android and iOS.
The app under test is [Sauce Labs My Demo App](docs/app-analysis.md).

Work in progress: Android first, iOS next.

## Stack

Java 25, Maven, TestNG, AssertJ, Allure, Appium 4 (java-client 11, UiAutomator2).

## Run

Prerequisites: JDK 25, Maven, Node.js with Appium 4 and the UiAutomator2 driver, Android SDK with an emulator.
See [docs/setup-windows.md](docs/setup-windows.md).

```powershell
.\scripts\download-apps.ps1
mvn test
```

No manual steps are needed before the run:
- the tests start the Appium server before the suite and stop it at the end (`@BeforeSuite`/`@AfterSuite` in `BaseTest`, `AppiumServer`, log in `target/appium.log`);
  an Appium server that is already running, e.g. for the Inspector, is reused and left running;
- Appium boots the emulator (`appium:avd` in the capabilities) when no device is running.

Useful options:
- `mvn test -Dretry.count=1`: retry a failed test once (used in CI);
- `mvn test -Dandroid.emulator.shutdown=false`: keep the emulator running after the tests.

Report:
- console: every test with its progress (`STARTED [1/7]`, `PASSED [1/7]`) and the elements it waited for;
- Allure: `mvn allure:serve` opens the report; a failed test has a screenshot and the page source
  (XML of the screen) in its tear down.

Configuration:
- `src/main/resources/config.properties`: platform, Appium server, waits; every key can be overridden with `-Dkey=value`;
- `src/main/resources/capabilities/<platform>.json`: session capabilities, in the same format as in the Appium Inspector.

## Page objects

One page object serves both platforms. Each element is a map with a locator per platform;
`ElementActions` picks the locator of the platform the tests run on (`findBy`) and waits for the element
(FluentWait with polling, no `Thread.sleep`, no implicit wait):

```java
private static final Map<Platform, By> TITLE = Map.of(
        IOS, AppiumBy.accessibilityId("title"),
        ANDROID, AppiumBy.id("productTV"));

public String getTitle() {
    return actions.getText(TITLE);
}
```
