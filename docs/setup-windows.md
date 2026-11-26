# Local setup on Windows (Android)

Everything installs per user, no admin rights needed. Versions used when the setup was done (2026-11-26):

| Tool                     | Version                                   |
|--------------------------|-------------------------------------------|
| JDK                      | 25 (Temurin)                              |
| Maven                    | 3.9.x                                     |
| Node.js                  | 26 (Appium 4 needs ^22.22.2, ^24.15.0 or >=26) |
| Android cmdline-tools    | 23.0                                      |
| Android platform-tools   | 37.0.3                                    |
| Android Emulator         | 37.3.10                                   |
| Build-tools              | 37.0.0                                    |
| System image             | `android-36;google_apis;x86_64` (Android 16) |
| Appium                   | 4.0.1                                     |
| UiAutomator2 driver      | 10.4.0                                    |
| Inspector plugin         | 2026.11.6                                 |

## 1. Hardware acceleration

The emulator uses WHPX (Windows Hypervisor Platform). Check:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -accel-check
```

Expected: `WHPX(...) is installed and usable.` If not, enable "Windows Hypervisor Platform" in Windows Features (admin, reboot).

## 2. Android SDK without Android Studio

1. Download `commandlinetools-win-<build>_latest.zip` from https://developer.android.com/studio#command-line-tools-only
2. Unzip it so that the tools end up in `%LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest\bin`.
3. Accept the licenses and install the packages:

```powershell
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
& "$sdk\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root=$sdk --licenses
& "$sdk\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root=$sdk "platform-tools" "emulator" "build-tools/37.0.0" "platforms/android-36" "system-images/android-36/google_apis/x86_64"
```

4. Environment variables (user scope; open a new terminal or restart the IDE afterwards):

```powershell
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
[Environment]::SetEnvironmentVariable('ANDROID_HOME', $sdk, 'User')
$path = [Environment]::GetEnvironmentVariable('Path', 'User')
[Environment]::SetEnvironmentVariable('Path', "$path;$sdk\platform-tools;$sdk\emulator;$sdk\cmdline-tools\latest\bin", 'User')
```

`JAVA_HOME` must point to the JDK as well.

## 3. Emulator

```powershell
avdmanager create avd -n Pixel_API_36 -k "system-images;android-36;google_apis;x86_64" -d pixel_8
```

Changed in `%USERPROFILE%\.android\avd\Pixel_API_36.avd\config.ini`: `hw.ramSize=4096`, `hw.gpu.enabled=yes`, `hw.keyboard=yes`.

Start it:

```powershell
emulator -avd Pixel_API_36 -no-snapshot-save -no-boot-anim
```

## 4. Appium

```powershell
npm install -g appium@4
appium driver install uiautomator2
appium plugin install inspector
appium driver doctor uiautomator2
```

The doctor reports only optional fixes (bundletool, ffmpeg, GStreamer); none of them is needed here.

Start the server with the Inspector plugin:

```powershell
appium --use-plugins=inspector
```

Inspector: http://127.0.0.1:4723/inspector (no separate desktop app needed).

## 5. App under test

The APK is not committed. Download it to `apps/` (ignored by Git):

```powershell
gh release download 2.3.0 -R saucelabs/my-demo-app-android -p "mda-2.3.0-27.apk" -D apps
```

Capabilities for the Inspector:

```json
{
  "platformName": "Android",
  "appium:automationName": "UiAutomator2",
  "appium:app": "C:/path/to/appium-e2e/apps/mda-2.3.0-27.apk",
  "appium:appWaitActivity": "*"
}
```

Use forward slashes in the path inside JSON.

## Verification

Done on Pixel_API_36 (Android 16, x86_64): a session installs and starts the app, the catalog screen
shows "Products" (`com.saucelabs.mydemoapp.android:id/productTV`), page source and screenshot work,
and the only context is `NATIVE_APP`.
