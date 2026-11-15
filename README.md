# KotlinAudio for Youradio Talk

This branch extracts the Media3-based KotlinAudio implementation from the Youradio Talk RNTP fork. It preserves the playback sources from [`czekFREE/react-native-track-player` at `cf924e75`](https://github.com/czekFREE/react-native-track-player/tree/cf924e75/android/src/main/java/com/doublesymmetry/kotlinaudio). The only changes to those 29 files are the two imports of the library-local development logger.

The `kotlin-audio` Android library builds independently of React Native. Its public packages remain `com.doublesymmetry.kotlinaudio`. RNTP owns the React Native bridge and its playback service registration. The library manifest does not register another service.

## Local integration

Youradio Talk checks out this fork as the `packages/kotlin-audio` Git submodule. Its Android settings include the module:

```gradle
include ':kotlin-audio'
project(':kotlin-audio').projectDir = file('../packages/kotlin-audio/kotlin-audio')
```

RNTP declares the local dependency:

```gradle
implementation project(':kotlin-audio')
```

The consuming Android root project provides `compileSdkVersion`, `targetSdkVersion`, and `minSdkVersion` in `rootProject.ext`, plus the Android and Kotlin Gradle plugins. The standalone build uses SDK 36, minimum SDK 24, AGP 8.12.0, Kotlin 2.1.20, and Gradle 9.3.1 to match the host app.

## Dependencies

The extraction keeps the existing runtime dependency pins. Media3 common, ExoPlayer, session, HLS, DASH, and SmoothStreaming use 1.8.0. AndroidX Core KTX uses 1.13.1, legacy AndroidX Media uses 1.7.0, Guava uses 33.0.0-android, coroutines core and Android use 1.7.3, and Timber uses 5.0.1. The Android coroutine dependency supplies the main-thread dispatcher used by the player. The host app can resolve higher compatible versions through its other dependencies.

Coil, Coil transformations, Lifecycle, Compose, LocalBroadcastManager, and AndroidX Test rules are not dependencies of this module. They remain owned by RNTP or its host app where applicable.

The library-local logger preserves RNTP's debug-only logging. JVM tests exercise both debug output and release suppression.

### Update candidates

Stable release versions were checked on November 15, 2026. The YRT column records the app's resolved release dependencies. Declared library pins can resolve to higher versions in the host app. These candidates have not been upgraded or playback-tested in this extraction.

| Dependency                                                    | KotlinAudio declaration | YRT release    | Candidate and scope                                                                                                                                                                                                                                       |
| ------------------------------------------------------------- | ----------------------- | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Media3 common, ExoPlayer, session, HLS, DASH, SmoothStreaming | 1.8.0                   | 1.8.0          | [1.11.1](https://developer.android.com/jetpack/androidx/releases/media3). Upgrade together with RNTP's Media3 dependencies and verify playback, buffering, audio focus, and media controls. The newer release uses Kotlin 2.2.0 and includes API changes. |
| Guava                                                         | 33.0.0-android          | 33.3.1-android | [33.8.0-android](https://github.com/google/guava/releases/tag/v33.8.0). A routine update candidate shared with RNTP.                                                                                                                                      |
| Coroutines core and Android                                   | 1.7.3                   | 1.9.0          | [1.11.0](https://github.com/Kotlin/kotlinx.coroutines/releases/tag/1.11.0). Align with RNTP's coroutines-guava and check Kotlin compatibility. Upstream builds with Kotlin 2.2.20.                                                                        |
| AndroidX Core KTX                                             | 1.13.1                  | 1.18.0         | [Core 1.19.1](https://developer.android.com/jetpack/androidx/releases/core). Kotlin extensions moved into core, so the core-ktx artifact is now an empty compatibility artifact. Check the host's AndroidX and SDK requirements together.                 |
| Legacy AndroidX Media                                         | 1.7.0                   | 1.7.0          | [1.8.0](https://developer.android.com/jetpack/androidx/releases/media). This release deprecates the library. KotlinAudio still uses its audio-focus APIs, and RNTP uses its rating APIs. Removing it requires a code migration.                           |
| Timber                                                        | 5.0.1                   | 5.0.1          | Already at the [latest release](https://github.com/JakeWharton/timber/releases/tag/5.0.1).                                                                                                                                                                |

The library's JVM tests use JUnit 4.13.2. Its standalone AGP, Kotlin, Gradle, and SDK settings match the host app. Review those as an app toolchain update rather than changing the library in isolation. The archived JUnit 5 instrumentation dependencies are no longer part of the active build.

RNTP still owns artwork loading through Coil 2.4.0 and Wasabeef transformations 1.0.6. [Coil 2.7.0](https://github.com/coil-kt/coil/releases/tag/2.7.0) is the smaller upgrade. [Coil 3.6.3](https://github.com/coil-kt/coil/releases/tag/3.6.3) requires the [Coil 3 migration](https://coil-kt.github.io/coil/upgrading_to_coil3/), including the artwork loader and network setup. RNTP's Lifecycle process/runtime dependencies resolve to 2.8.7, with [2.11.0](https://developer.android.com/jetpack/androidx/releases/lifecycle) available after a host compatibility check.

RNTP's activity-compose 1.9.3, Compose UI/graphics 1.6.8, LocalBroadcastManager 1.1.0, and production AndroidX Test rules 1.6.1 remain removal candidates. No source usage was found in the current RNTP Android implementation. They were retained here to keep the extraction separate from dependency cleanup.

## Verification

From the Youradio Talk `android` directory:

```sh
./gradlew :kotlin-audio:testDebugUnitTest :kotlin-audio:testReleaseUnitTest :app:assembleRelease
```

From this repository, with Android SDK 36 installed and `ANDROID_HOME` or `local.properties` configured:

```sh
./gradlew :kotlin-audio:testDebugUnitTest :kotlin-audio:testReleaseUnitTest :kotlin-audio:assembleRelease
```

The upstream ExoPlayer 2 instrumentation suite is preserved under `legacy/androidTest`. It does not validate this branch's Media3 implementation and is not part of the active source sets. The upstream example also targets the old API and is excluded from this branch's standalone settings. Porting those examples and device tests is a separate migration.

## Upstream documentation

The following upstream documentation is retained for attribution and historical reference. Its example and Maven installation instructions describe the older ExoPlayer 2 implementation.

## KotlinAudio

[![](https://jitpack.io/v/doublesymmetry/KotlinAudio.svg)](https://jitpack.io/#doublesymmetry/KotlinAudio)

KotlinAudio is an Android audio player written in Kotlin, making it simpler to work with audio playback from streams and files.

Inspired by [SwiftAudioEx](https://github.com/doublesymmetry/SwiftAudioEx). Our aim is to have feature parity with the iOS equivalent.

<div align="left" valign="middle">
<a href="https://runblaze.dev">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://www.runblaze.dev/logo_dark.png">
   <img align="right" src="https://www.runblaze.dev/logo_light.png" height="102px"/>
 </picture>
</a>

<br style="display: none;"/>

_[Blaze](https://runblaze.dev) sponsors KotlinAudio by providing super fast Apple Silicon based macOS Github Action Runners. Use the discount code `RNTP50` at checkout to get 50% off your first year._

</div>

## Example

To see the audio player in action, run the example project!
To run the example project, clone the repo, then open in Android Studio.
Choose "kotlin-audio-example" in the run target and run it in a simulator
(or on an actual device).

## Requirements

minSDK 21

## Installation

### Gradle

```gradle
implementation 'com.github.doublesymmetry:kotlinaudio:v2.0.0'
```

## Usage

### AudioPlayer

To get started playing some audio:

```swift
let player = AudioPlayer()
let audioItem = DefaultAudioItem(audioUrl: "someUrl", type: MediaType.DEFAULT)
player.load(item: audioItem, playWhenReady: true) // Load the item and start playing when the player is ready.
```

To listen for events in the `AudioPlayer`, subscribe to events found in the `event` property of the `AudioPlayer`.
To subscribe to an event:

```kotlin
// jetpack compose
val state = player.event.stateChange.collectAsState(initial = AudioPlayerState.IDLE)

// normal
player.event.stateChange.collect {}
```

#### QueuedAudioPlayer

The `QueuedAudioPlayer` is a subclass of `AudioPlayer` that maintains a queue of audio tracks.

```swift
let player = QueuedAudioPlayer()
let audioItem = DefaultAudioItem(audioUrl: "someUrl", type: MediaType.DEFAULT)
player.add(item: audioItem, playWhenReady: true) // Since this is the first item, we can supply playWhenReady: true to immedietaly start playing when the item is loaded.
```

When a track is done playing, the player will load the next track and update the queue.

##### Navigating the queue

All `AudioItem`s are stored in either `previousItems` or `nextItems`, which refers to items that come prior to the `currentItem` and after, respectively. The queue is navigated with:

```swift
player.next() // Increments the queue, and loads the next item.
player.previous() // Decrements the queue, and loads the previous item.
player.jumpToItem(index:) // Jumps to a certain item and loads that item.
```

##### Manipulating the queue

```swift
 player.remove(index:) // Remove a specific item from the queue.
 player.removeUpcomingItems() // Remove all items in nextItems.
```

## License

KotinAudio is available under the MIT license. See the LICENSE file for more info.
