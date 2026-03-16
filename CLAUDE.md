# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Insight-android is a single-module Android app built with Jetpack Compose and Material 3. It targets API 28–36 and uses Kotlin with Java 11 compatibility.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install debug APK on connected device
./gradlew testDebugUnitTest      # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew lintDebug              # Run Android Lint
./gradlew clean                  # Clean build outputs
```

## Architecture

- **Single module** (`app`) with namespace `com.example.insight_android`
- **Single activity** (`MainActivity`) using Compose with edge-to-edge display
- **Theme system** in `ui/theme/` — Material 3 with dynamic color support (Android 12+), light/dark schemes
- **Build config**: Gradle 9.1.0, AGP 9.0.1, Kotlin 2.0.21, Compose BOM 2024.09.00
- **Dependency versions** are centralized in `gradle/libs.versions.toml`
- **No DI, networking, or persistence** frameworks are configured yet
