# 99 — Báo cáo sau nâng cấp

Ngày: 2026-09-23. Nhánh `chore/upgrade-20260923`. Tag `pre-upgrade-baseline` = `e14a3d3`. Không push.

`compileSdk` **37**, `targetSdk` **37**, `minSdk` app **24**. Không rollback API 37. `warningsAsErrors` không bật. R8 vẫn tắt. Không đổi layout, theme, màu, string hiển thị, icon trong app, hay navigation.

Không có emulator/thiết bị trong session này, nên chưa có ảnh UI và chưa chạy instrumentation.

## 1. Build, test, lint, APK

Lệnh cuối, cùng kiểu baseline:

```
./gradlew assembleDebug testDebugUnitTest lintDebug --warning-mode=all
```

| | Baseline (`e14a3d3`) | Sau nâng cấp |
| --- | --- | --- |
| Kết quả | BUILD SUCCESSFUL in 2m 59s | BUILD SUCCESSFUL in 8m 32s |
| Unit test | 1/1 (`ExampleUnitTest`) | 1/1. Library unit test vẫn NO-SOURCE |
| Compiler warning | 26 | **0** (full `compileDebugKotlin --rerun-tasks`) |
| Gradle deprecation (`--warning-mode=all`) | 0 | 0 |
| Lint error | 1 (`StringFormatInvalid`) | **0** |
| Lint warning | 109 | **4** (chỉ `:app`) |
| Debug APK | 24,092,317 bytes | **39,193,896 bytes** |
| Method ids | 282,901 | **389,168** |
| compile / target / min | 34 / 34 / 24 | **37 / 37 / 24** |

Thời gian 8m 32s gồm lint analyze trên graph dependency mới, không phải incremental `assembleDebug` (lần đó vài giây). APK và method count tăng vì R8 tắt và Firebase BOM / Ads / Compose mới nặng hơn. Không bật minify trong đợt này.

Lint còn lại, cố ý không sửa vì đụng hình:

- `MonochromeLauncherIcon` (2): thêm lớp monochrome sẽ đổi icon launcher dạng themed.
- `VectorPath` (1): `ic_brightness.xml` path dài 3194 ký tự. Rút path sẽ đổi nét icon.
- `IconMissingDensityFolder` (1): ảnh skin/nền chỉ có `drawable-mdpi`. Thư mục không density cũ cũng bị scale như mdpi, nên kích thước trên màn hình giữ nguyên. Copy cùng file sang hdpi/xhdpi/xxhdpi sẽ làm ảnh nhỏ đi.

## 2. Version

| | Trước | Sau |
| --- | --- | --- |
| Gradle | 8.2 | 9.7.1 |
| AGP | 8.2.2 | 9.4.1 |
| Kotlin / Compose plugin | 1.9.0 / compiler 1.5.1 | 2.4.20 |
| JDK bytecode | 1.8 | 17 |
| KSP | không (kapt) | 2.3.12. Hilt và Epoxy qua KSP. `lifecycle-compiler` bỏ |
| Compose BOM | 2023.08.00 | 2026.09.00 (Material3 1.4.0, UI 1.12.1) |
| Hilt | 2.50 | 2.60.1 |
| Navigation / Hilt navigation | transitive | 2.10.1 / 1.4.0 |
| Firebase | analytics 22.0.0, firestore 25.0.0 | BOM 34.19.0 |
| Play Billing | 6.2.0 | 9.1.0 |
| Ads | cũ hơn | 25.5.0 |
| CameraX | 1.4.0-alpha04 | 1.6.2. Bỏ `camera-mlkit-vision` (không dùng) |
| core-ktx / lifecycle / activity | cũ | 1.19.0 / 2.11.0 / 1.13.0 |
| appcompat / material / splash | cũ, splash alpha | 1.8.0 / 1.14.0 / 1.2.0 |
| security-crypto | 1.1.0-alpha06 | 1.1.0 |
| Coil | 2.x | **2.7.0** (không lên Coil 3) |
| colorpicker | 0.0.15 | **0.0.15** |
| accompanist-permissions | giữ | 0.37.3 |
| Epoxy / Timber / Gson | | 5.2.1 / 5.0.1 / 2.14.0 |
| Test | JUnit 4 | JUnit 4.13.2, espresso 3.7.0, androidx.test.ext junit 1.3.0 |
| Library minSdk | camposer và billing-utils 21 | **24** |
| Đã gỡ | | `work-runtime`, `liveevent` |

Catalog: `gradle/libs.versions.toml`. AGP 9 không apply `org.jetbrains.kotlin.android`. `buildFeatures.resValues = true` vì ad unit id nằm trong `resValue`.

## 3. Hành vi platform

### 3.1 target 35 — edge-to-edge

`enableEdgeToEdge` với status và navigation bar style tối, màu `color_app` (`#191919`). Nội dung root của `MainActivity` có `safeDrawingPadding()`. Scaffold đặt `contentWindowInsets` về 0 để không pad hai lần. Splash pad `android.R.id.content` và consume inset. `android:elegantTextHeight=false`. SideEffect không còn ghi `statusBarColor` (bị bỏ qua khi edge-to-edge). Theme XML vẫn giữ màu bar cho OS cũ.

### 3.2 target 36 — 16KB

Không có `screenOrientation` / `resizeableActivity` để gỡ. `zipalign -c -P 16` trên debug APK: verification successful. ELF `LOAD` của 4 `.so` arm64 (`libandroidx.graphics.path.so`, `libdatastore_shared_counter.so`, `libimage_processing_util_jni.so`, `libsurface_util_jni.so`) có `p_align` **0x4000** (16 KiB).

### 3.3 target 37

Không khóa orientation, không `maxAspectRatio`, không widget, không WebView, không NDK riêng. Activity đã khai báo `configChanges` cho orientation/screenSize/screenLayout nên xoay không recreate.

Morse: `MorseViewModel` nghe `ProcessLifecycleOwner`. Khi process vào background, playback dừng qua `setValuePlayState(false)` và đèn pin Morse tắt. Không thêm foreground-service notification. Đèn pin màn Home không bị dừng.

Permission mới so với baseline: `READ_MEDIA_VISUAL_USER_SELECTED`. Cần cho quyền ảnh một phần trên Android 14+ khi app đã xin `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO`. Không thêm quyền dangerous khác.

## 4. Việc cố ý chưa làm

- Coil 3 và thay color picker: default hình ảnh/UI đổi, để đợt sau.
- `EncryptedSharedPreferences` / `MasterKey` bị deprecate ở security-crypto 1.1.0. Vẫn là API đọc file mã hóa đang có trên máy. Suppress tại chỗ, không đổi scheme (đổi scheme làm mất language/skin/premium đã lưu).
- `AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize` deprecated. API thay thế (`getLargeAnchoredAdaptiveBannerAdSize`) cao hơn, nên giữ size cũ và suppress.
- `setSubscriptionReplacementMode(int)` và `SERVICE_TIMEOUT` không có API thay trong Billing 9.1.0. Suppress.
- Material3 1.4: app gắn `Typography` riêng, **không** gắn `Shapes` trong `Shape.kt` vào `MaterialTheme`. Default shape/elevation của thư viện có thể lệch pixel. Chưa chụp màn hình để so.
- `IconMissingDensityFolder`, monochrome launcher, vector brightness: xem mục 1.

## 5. Smoke test thủ công

Chưa chạy trên thiết bị. Nên kiểm tra:

- Splash, nút back trên splash không thoát.
- Home: bật/tắt đèn, tiếng click, xoay máy, gesture nav và 3 nút.
- Settings, đổi ngôn ngữ.
- Brightness và color picker.
- Compass.
- Morse: phát, đưa app ra background (phải dừng), mở lại.
- Camera → gallery → preview, kể cả quyền ảnh một phần (Android 14+).
- Skin, Pro / billing.
- Tablet hoặc cửa sổ `sw >= 600dp` (API 37 không còn khóa resize).
- API 24, 34, và 37 nếu có máy.

## 6. Gợi ý, không làm trong đợt này

Renovate hoặc Dependabot, convention plugin, bật R8 khi đã có mapping, baseline profile, configuration cache, CI (`assembleDebug testDebugUnitTest lintDebug`), detekt, screenshot test, StrictMode, layout thích ứng cho màn lớn.
