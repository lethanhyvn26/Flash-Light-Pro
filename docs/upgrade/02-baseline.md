# 02 — Baseline

Ngày: 2026-09-23. Nhánh `chore/upgrade-20260923`. Tag `pre-upgrade-baseline` trỏ commit `e14a3d3` (code gốc, trước docs này).

JDK: Temurin 17.0.19. SDK: `~/Library/Android/sdk`. Không có emulator session, nên không có ảnh chụp UI.

## Lệnh

```
./gradlew assembleDebug testDebugUnitTest lintDebug --warning-mode=all
```

Kết quả: **BUILD SUCCESSFUL in 2m 59s**.

## Test

| | |
| --- | --- |
| `testDebugUnitTest` | **1/1 pass** (`ExampleUnitTest`) |
| Library unit tests | NO-SOURCE |
| Instrumentation | không chạy (cần thiết bị) |

## Compiler

26 warning Kotlin (`w:`), 0 error.

- Billing: `SERVICE_TIMEOUT` deprecated, `setReplaceProrationMode` deprecated, unused param
- CameraX: `OutputSize` deprecated (14)
- `OnLifecycleEvent` deprecated (2)
- Unused parameter / variable (MainActivity `db`, vài `modifier`, `useCache`, `step`)
- languagepicker `useCache` unused

Gradle `--warning-mode=all`: không có dòng deprecation của Gradle trong log baseline.

## Lint (`lintDebug`, abortOnError = false)

| | |
| --- | --- |
| Error | **1** — `StringFormatInvalid` trên `str_pro_sale_off` (`50% GIẢM GIÁ`, `% G` bị hiểu là format) |
| Warning | **109** |

Phổ biến: `GradleDependency` 45, `IconLocation` 20, `ObsoleteSdkInt` 12, `VectorPath` 5, `ContentDescription` 4 (đã disable trong app nhưng lint vẫn báo), `BinaryOperationInTimber` 3, `LogNotTimber` 3, `KaptUsageInsteadOfKsp` 2, `Overdraw` 2, `UnusedResources` 2, `MonochromeLauncherIcon` 2, `ClickableViewAccessibility` 2, `RtlEnabled` 2, và 1 mỗi loại: `ObsoleteLintCustomCheck`, `OldTargetApi`, `StringFormatCount`, `LifecycleAnnotationProcessorWithJava8`, `StaticFieldLeak`.

## APK debug

| | |
| --- | --- |
| File | `app/build/outputs/apk/debug/app-debug.apk` |
| Size | **24,092,317 bytes** (~23.0 MiB) |
| Method ids (tổng mọi `classes*.dex`) | **282,901** |
| compileSdk / target / min | 34 / 34 / 24 |
| Native | `libimage_processing_util_jni.so` cho arm64-v8a, armeabi-v7a, x86, x86_64 (CameraX). Không có `.so` khác |
| R8 | tắt |

Release AAB không build ở baseline (release dùng ad id production, minify tắt). So sánh sau sẽ dùng debug APK cùng kiểu.

## Permission trong APK

App: `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `AD_ID`, `READ_MEDIA_IMAGES`, `READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO`, `CAMERA`, `WRITE_EXTERNAL_STORAGE` maxSdk 29, `READ_EXTERNAL_STORAGE` maxSdk 32, `BILLING`.

Do SDK kéo vào: `WAKE_LOCK`, `ACCESS_ADSERVICES_ATTRIBUTION`, `ACCESS_ADSERVICES_AD_ID`, `ACCESS_ADSERVICES_TOPICS`, `FOREGROUND_SERVICE`, `BIND_GET_INSTALL_REFERRER_SERVICE`, `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`.
