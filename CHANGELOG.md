# Changelog - Seedkeeper-Android

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.4]:

* Target Android 15 (API level 35), as required by Google Play
* Support edge-to-edge display: content no longer overlaps the status and navigation bars
* Patch status bar icons not visible
* Upgrade Android Gradle Plugin to v8.13.2
* Add Spanish translation (thanks to @esixce, PR #73)
* Support per-app language preferences (Android 13+): select the app language in system settings or from the app Settings screen
* Fix UI display issues on small screens (issue #63)
* Allow up to 32 characters in the password generator (issue #66)

## [0.1.3]:

* Patch minor UI display bug: mnemonic descriptor not showing

## [0.1.2]:

* Remove NDEF deep linking (not used)

## [0.1.1]:

* Various minor improvements and fixes, mostly UI & typos
* Patch: Mnemonic generated from the legacy desktop app was not displayed correctly

## [0.1.0]:

* Initial beta release