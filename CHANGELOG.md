# Changelog - Seedkeeper-Android

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.5]:

* Target Android 16 (API level 36), as required by Google Play
* Support edge-to-edge display: content no longer overlaps the status and navigation bars
* Keep screen state when rotating or resizing on tablets and foldables (Android 16 ignores the portrait lock on large screens)
* Patch status bar icons not visible
* Upgrade Android Gradle Plugin to v8.13.2
* Add Spanish translation (thanks to @esixce, PR #73)
* Support per-app language preferences (Android 13+): select the app language in system settings or from the app Settings screen
* Fix UI display issues on small screens (issue #63)
* Allow up to 32 characters in the password generator (issue #66)
* Keep NFC reader mode active while the app is open, so other NFC apps no longer open when tapping the card (issue #67)
* Cancelling the NFC dialog now cancels the pending card action, show a warning when NFC is disabled
* Clear card data (secret list, PIN, card info) from memory on refresh and after 5 minutes of inactivity (issue #76)
* Clear secrets copied to the clipboard after 30 seconds (only if the clipboard still contains the copied secret, when it can be checked)
* Fix backup reporting success with 0 secrets saved when the backup card has no memory left: errors are now reported and listed in the backup result screen
* Improve readability of the backup error details and keep them clear of the bottom button
* Fix first PIN check failing (error 0x9C30) after an interrupted card operation, e.g. a backup that ran out of memory
* Remove secret labels from logs: card library logs disabled unless debug mode is on, debug logs no longer include labels and are cleared with card data
* Disable Android backup and device transfer of app data
* Update Satochip-Java library to v0.3.6: no sensitive data in logs, BouncyCastle 1.86, more reliable secure channel (detailed library logs available in debug mode)
* Replace bitcoinj with a lightweight built-in BIP39 implementation (tested against the official vectors); passphrases are now NFKD-normalized as required by BIP39

## [0.1.4]:

(Unreleased, same as 0.1.5)

## [0.1.3]:

* Patch minor UI display bug: mnemonic descriptor not showing

## [0.1.2]:

* Remove NDEF deep linking (not used)

## [0.1.1]:

* Various minor improvements and fixes, mostly UI & typos
* Patch: Mnemonic generated from the legacy desktop app was not displayed correctly

## [0.1.0]:

* Initial beta release