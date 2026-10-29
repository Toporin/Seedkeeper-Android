# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android companion app (Kotlin + Jetpack Compose) for the Satochip **Seedkeeper** NFC smartcard, which stores seedphrases, passwords, wallet descriptors and free text. Single Gradle module `:app`, package `org.satochip.seedkeeper`, minSdk 24 / targetSdk 34, Java/JVM target 1.8.

## Build & test

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # install on connected device
./gradlew testDebugUnitTest             # JVM unit tests
./gradlew testDebugUnitTest --tests "org.satochip.seedkeeper.ExampleUnitTest"   # single test class
./gradlew connectedDebugAndroidTest     # instrumented tests (device required)
./gradlew lint
```

- The build applies the Google Services and Firebase Crashlytics plugins, so `app/google-services.json` must exist. It is gitignored; `app/google-services sample.json` is the template to copy.
- Only placeholder tests exist. NFC flows need a physical device and a real card to exercise.
- Dependency versions live in the version catalog (`gradle/libs.versions.toml`).
- When releasing, bump `versionName`/`versionCode` in `app/build.gradle.kts` (`versionCode = 10000x + 100y + z` for `x.y.z`) and add an entry to `CHANGELOG.md`.

## Card communication libraries

The card protocol comes from [Satochip-Java](https://github.com/Toporin/Satochip-Java), used as local artifacts under `app/libs/`: `satochip-lib-<ver>.jar` (`org.satochip.client.*`: `SatochipCommandSet`, `seedkeeper.SeedkeeperSecretObject/Header/Status`, etc.) and `satochip-android-<ver>.aar` (`org.satochip.android.NFCCardManager`). Only the versions named in `app/build.gradle.kts` are used; the older jars in that folder are ignored. Local artifacts carry no POM, so satochip-lib's BouncyCastle dependency (`bcprov-jdk15to18`) is declared in the app. The library passes its own BouncyCastle provider instance to the JCA calls, so nothing needs to be registered as "BC". bitcoinj supplies BIP39 (`MnemonicCode`).

## Architecture

All NFC work goes through a global singleton state machine. One NFC operation runs like this:

1. A Compose view calls `SharedViewModel.scanCardForAction(activity, NfcActionType.X)`. Before that it stores any inputs the action needs directly on `NFCCardService` through ViewModel setters (`pinString`, `backupPinString`, `newPinString`, `secretData`, `currentSecretHeader`, …).
2. `NFCCardService.scanCardForAction` (`services/NFCCardService.kt`) starts an `NFCCardManager` and enables NFC reader mode.
3. When a card is tapped, `SatochipCardListenerForAction.onConnected` (`services/SeedkeeperCardListener.kt`) builds a `SatochipCommandSet` and calls `NFCCardService.initialize(cmdSet)`. This dispatches on `actionType` to a private method (scanCard, importSecret, exportSecret, backup steps, factory reset…), then disables reader mode.
4. Results come back as `MutableLiveData` on `NFCCardService`. The most important is `resultCodeLive: NfcResultCode`; others include `secretHeaders`, `currentSecretObject`, `cardLabel`, `authenticityStatus` and the backup progress values.
5. `SharedViewModel` (`viewmodels/SharedViewModel.kt`, one instance created in `MainActivity`) mirrors each LiveData into Compose state with `observeForever`. Views react to `viewModel.resultCodeLive` in `LaunchedEffect` blocks and reset it with `setResultCodeLiveTo(NfcResultCode.NONE)`.

To add a new card operation: add an `NfcActionType` value, a branch in `NFCCardService.initialize`, the result codes it needs in `NfcResultCode`, and handling in the view.

Other key pieces:
- **Navigation** (`Navigation.kt`): type-safe Compose Navigation. Routes are `@Serializable` objects/data classes defined at the bottom of the file. It also handles global redirects: `REQUIRE_SETUP` / `REQUIRE_SETUP_FOR_BACKUP` result codes navigate to `PinEntryView`. The start destination is the welcome flow on first launch, otherwise `HomeView`.
- **Backup** is a multi-step flow across two cards: scan the backup card, export the master's secrets encrypted to the backup card's authentikey, then import them into the backup card. Each step is its own `NfcActionType`, and the master and backup cards have separate PIN and status fields.
- **Secret parsing**: `parsers/SecretDataParser.kt` decodes raw secret bytes into `data/SecretData` according to `SeedkeeperSecretType`. `NFCCardService.createSecretObject` does the reverse for import. Legacy formats (e.g. mnemonics written by the desktop app) are handled there.
- **Logging**: use `SatoLog.d/i/w/e` instead of `android.util.Log`. It only logs, and keeps an in-app log list, when debug mode is on (a Settings toggle stored in the `"seedkeeper"` SharedPreferences under `SeedkeeperPreferences`).
- **UI**: screens live in `ui/views/<feature>/`, reusable composables in `ui/components/<feature>/`, theme in `ui/theme/`. Strings are localized in `res/values` and `res/values-fr`; add new strings to both. The memorable-password word lists are in `assets/password-replacement*.txt`.
