# Downloads the apps under test from the Sauce Labs GitHub releases into apps/ (ignored by Git).
# Requires the GitHub CLI (gh). Usage: download-apps.ps1 [android|ios|all], default all.
param(
    [ValidateSet('android', 'ios', 'all')]
    [string] $Platform = 'all'
)
$ErrorActionPreference = 'Stop'

$apps = Join-Path $PSScriptRoot '..\apps'
New-Item -ItemType Directory -Force -Path $apps | Out-Null

function Get-App([string] $repo, [string] $file) {
    $target = Join-Path $apps $file
    if (Test-Path $target) {
        Write-Host "Already downloaded: $target"
        return
    }
    Write-Host "Downloading $file ..."
    gh release download 2.3.0 --repo $repo --pattern $file --dir $apps
    if ($LASTEXITCODE -ne 0) { throw "Download of $file failed" }
}

if ($Platform -in 'android', 'all') {
    Get-App 'saucelabs/my-demo-app-android' 'mda-2.3.0-27.apk'
}
if ($Platform -in 'ios', 'all') {
    # Simulator build (x86_64 + arm64); Appium installs the .app straight from the zip
    Get-App 'saucelabs/my-demo-app-ios' 'SauceLabs-Demo-App.Simulator.zip'
}
