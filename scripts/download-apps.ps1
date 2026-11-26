# Downloads the app under test from the Sauce Labs GitHub releases into apps/ (ignored by Git).
# Requires the GitHub CLI (gh).
$ErrorActionPreference = 'Stop'

$apps = Join-Path $PSScriptRoot '..\apps'
New-Item -ItemType Directory -Force -Path $apps | Out-Null

$android = Join-Path $apps 'mda-2.3.0-27.apk'
if (Test-Path $android) {
    Write-Host "Already downloaded: $android"
} else {
    Write-Host 'Downloading the Android app ...'
    gh release download 2.3.0 --repo saucelabs/my-demo-app-android --pattern 'mda-2.3.0-27.apk' --dir $apps
    if ($LASTEXITCODE -ne 0) { throw 'Download of the Android app failed' }
}
