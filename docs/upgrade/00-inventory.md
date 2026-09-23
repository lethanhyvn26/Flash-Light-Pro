# 00 — Inventory (Phase 0)

Ngày quét: **2026-09-23**. Chỉ đọc repo và đối chiếu Maven / docs chính thức. Không sửa code, không commit.

Mốc tham chiếu trong brief (18/09/2026) đã được kiểm lại. Chỗ lệch được ghi ở mục 2.

---

## 1. Cấu trúc project

| Hạng mục | Hiện trạng |
| --- | --- |
| Module | 4: `:app` (application), `:camposer` (library), `:languagepicker` (library), `:billing-utils` (library) |
| Dynamic feature / KMP | Không |
| Build logic | Không `buildSrc`, không convention plugin |
| Version catalog | **Chưa có** `gradle/libs.versions.toml`. Version nằm rải trong `build.gradle.kts` và `rootProject.extra` |
| DSL | Toàn bộ Kotlin DSL (`*.kts`). Không Groovy |
| CI | Không có `.github/` hay pipeline khác trong repo |
| Source | 94 file `.kt`, 1 file `.java` (`billing-utils/.../ErrorType.java`), 4 layout XML |
| Application id | `flashlight.phichung.com.torch` |
| versionName / versionCode | `1.1.6` / `6` |

Luồng UI (Compose `NavHost` trong `MainActivity`, start = Home):

Splash → Home → Settings, Brightness, Compass, Morse, Pro (bỏ quảng cáo / billing), Camera → Gallery → Preview, Skin. Language picker là View-system (`viewBinding` + Epoxy), gọi từ Settings.

---

## 2. Toolchain hiện tại

| Thành phần | Hiện tại | Stable mới nhất đã verify | Ghi chú |
| --- | --- | --- | --- |
| Gradle wrapper | **8.2** (`gradle-8.2-bin.zip`, 2024-02-19) | **9.7.1** (2026-08-19) | AGP 9.4 yêu cầu tối thiểu **9.6.0** (default cũng 9.6.0). Kotlin 2.4.20 ghi *fully compatible* tới **9.7.0**; bản mới hơn có thể có deprecation warning |
| AGP | **8.2.2** | **9.4.1** (POM 2026-09-18) | 9.5.0-alpha06 có trên Maven, **không** dùng. Brief nói 9.4.0 — stable mới hơn một patch |
| Kotlin | **1.9.0** | **2.4.20** (2026-09-07) | Khớp brief |
| KSP | Không dùng (đang **kapt**) | **2.3.12** (POM 2026-09-09) | Không thấy dòng release note nào ghi thẳng “supports Kotlin 2.4.20”. Docs KSP hiện ví dụ Kotlin 2.4.10 + KSP 2.3.10. Phải xác nhận bằng build |
| Compose compiler | `kotlinCompilerExtensionVersion = 1.5.1` | Plugin `org.jetbrains.kotlin.plugin.compose` đi kèm Kotlin 2.4.20 | Cú pháp cũ bị bỏ từ Kotlin 2.0 |
| Compose BOM | **2023.08.00** | **2026.09.00** (POM 2026-09-09) | Brief nói 2026.08.00. BOM mới hơn tồn tại. Material3 trong BOM này là **1.4.0** |
| JDK để chạy Gradle | Không khai báo toolchain. `sourceCompatibility` / `jvmTarget` = **1.8** | AGP 9.4 yêu cầu **JDK 17** để chạy build | Bytecode 1.8 có thể giữ tạm; nhiều AndroidX mới mặc định min compile theo JDK 17 |
| compileSdk | **34** (cả 4 module) | Mục tiêu **37** | AGP 9.4 hỗ trợ tối đa API 37 |
| targetSdk | **34** (chỉ `:app`) | Mục tiêu **37**, làm sau | |
| minSdk | app **24**, languagepicker **24**, camposer **21**, billing-utils **21** | Giữ **24** cho app | CameraX 1.6.x thuộc nhóm AndroidX đã nâng default minSdk **21 → 23** (từ 06/2025). `camposer` minSdk 21 sẽ phải kéo lên ≥ 23 khi lên CameraX 1.6. App đã là 24 nên **tệp user không đổi** nếu không đụng `app.minSdk` |
| buildToolsVersion | Không pin | AGP 9.4 default Build Tools **36.0.0** | Không cần khai báo tay |
| NDK / CMake | Không | AGP 9.4 default NDK 28.2.13676358 | Không áp dụng cho code mình. `.so` transitive: xem mục 7 |
| Jetifier / jcenter | Không | — | Đã sạch |
| AndroidX | `android.useAndroidX=true`, `android.nonTransitiveRClass=true` | Giữ | |

### Docs toolchain

- AGP 9.4.0 compatibility (Gradle 9.6.0, JDK 17, max API 37): https://developer.android.com/build/releases/agp-9-4-0-release-notes
- AGP 9.4.1 patch (Android Studio Quail 4 Patch 1, 2026-09-18): https://androidstudio.googleblog.com/2026/09/android-studio-quail-4-patch-1-now.html
- Bảng AGP ↔ Gradle: https://developer.android.com/build/releases/about-agp
- Lộ trình DSL / Variant API (opt-out gỡ ở AGP 10): https://developer.android.com/build/releases/gradle-plugin-roadmap
- Gradle 9.7.1: https://gradle.org/releases/ — release notes 9.6.0: https://docs.gradle.org/9.6.0/release-notes.html
- Kotlin 2.4.20 (2026-09-07), Gradle 7.6.3–9.7.0: https://kotlinlang.org/docs/whatsnew2420.html
- Compose compiler plugin: https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler
- Compose BOM mapping (2026.09.00 → Material3 1.4.0, UI 1.12.1): https://developer.android.com/develop/ui/compose/bom/bom-mapping
- Android 17 / API 37: https://developer.android.com/about/versions/17/behavior-changes-17 và https://developer.android.com/about/versions/17/behavior-changes-all và https://developer.android.com/blog/posts/android-17-is-here

Artifact tồn tại trên Maven (đã `HEAD`/`GET` metadata). “Latest” trên `maven-metadata.xml` đôi khi là alpha; cột dưới đây là **stable mới nhất**, không phải pre-release.

---

## 3. Bảng dependency

Khoảng cách major tính từ version đang dùng tới stable mới nhất. “Maintained” = còn release trong 18 tháng qua, trừ khi ghi chú khác.

### Build plugin

| group:artifact | Hiện tại | Stable mới nhất | Major | Maintain | Breaking | Release notes |
| --- | --- | --- | --- | --- | --- | --- |
| `com.android.tools.build:gradle` (plugin `com.android.application`) | 8.2.2 | 9.4.1 (2026-09-18) | 1 | Có | **Có.** AGP 9: built-in Kotlin, DSL mới, Variant API cũ bỏ (opt-out tới AGP 10). Repo này không gọi `applicationVariants` | https://developer.android.com/build/releases/agp-9-4-0-release-notes |
| `org.jetbrains.kotlin.android` | 1.9.0 | 2.4.20 (2026-09-07) | 1 | Có | **Có.** Compose compiler plugin; kapt vẫn chạy nhưng K2 + kapt là đường cũ | https://kotlinlang.org/docs/whatsnew2420.html |
| `com.google.dagger.hilt.android` | 2.50 | 2.60.1 (2026-07-06) | 0 | Có | Nhỏ trong 2.5x→2.6x. Hilt hỗ trợ KSP | https://github.com/google/dagger/releases/tag/dagger-2.60.1 |
| `com.google.gms:google-services` | 4.4.1 (qua `buildscript.classpath`) | 4.5.0 (2026-06-16) | 0 | Có | Thấp. Nên chuyển sang plugins DSL | https://developers.google.com/android/guides/google-services-plugin |
| `com.google.devtools.ksp` | không dùng | 2.3.12 (POM 2026-09-09) | — | Có | Chỉ đụng nếu bỏ kapt. KSP 2.3.12 nâng min AGP lên 8.12.0 | https://github.com/google/ksp/releases |

`com.android.library` được apply ở 3 module **không khai báo version** ở root `plugins {}`. Hiện gắn ngầm với AGP 8.2.2. Khi nâng AGP phải khai báo tường minh.

### AndroidX / Google

| group:artifact | Hiện tại | Stable mới nhất | Major | Maintain | Breaking | Release notes |
| --- | --- | --- | --- | --- | --- | --- |
| `androidx.compose:compose-bom` | 2023.08.00 | 2026.09.00 (2026-09-09) | n/a (BOM) | Có | **Có, và có rủi ro UI.** Material3 1.1.x → **1.4.0**, Compose UI → 1.12.1. Default shape / padding / elevation / typography của component có thể đổi | https://developer.android.com/develop/ui/compose/bom/bom-mapping |
| `androidx.core:core-ktx` | 1.12.0 | 1.19.0 (2026-06-03) | 0 | Có | Thấp với API app đang gọi | https://developer.android.com/jetpack/androidx/releases/core |
| `androidx.lifecycle:lifecycle-runtime-ktx` | **hai lần**: 2.7.0 và 2.6.2 | 2.11.0 (2026-06-17). 2.12.0-alpha03 tồn tại, bỏ | 0 | Có | `@OnLifecycleEvent` đã deprecated từ lâu; processor kapt đi kèm | https://developer.android.com/jetpack/androidx/releases/lifecycle |
| `androidx.lifecycle:lifecycle-process` | 2.6.2 | 2.11.0 | 0 | Có | Thấp | cùng link |
| `androidx.lifecycle:lifecycle-compiler` (kapt) | 2.6.2 | 2.11.0 | 0 | Có | Chỉ phục vụ `@OnLifecycleEvent` ở `MyApplication`. Có thể bỏ processor nếu đổi sang `DefaultLifecycleObserver` | cùng link |
| `androidx.activity:activity-compose` | 1.8.2 | 1.13.0 (2026-03-11). 1.14.0-alpha02 bỏ | 0 | Có | Thấp nếu không đụng API mới. Edge-to-edge helper nằm ở đây | https://developer.android.com/jetpack/androidx/releases/activity |
| `androidx.appcompat:appcompat` | 1.6.1 | 1.8.0 (2026-08-12) | 0 | Có | Thấp. Theme app kế thừa `Theme.AppCompat` | https://developer.android.com/jetpack/androidx/releases/appcompat |
| `com.google.android.material:material` | 1.11.0 (`:languagepicker`) | 1.14.0 (2026-05-13) | 0 | Có | **Rủi ro UI** trên dialog ngôn ngữ (widget Material View) | https://github.com/material-components/material-components-android/releases |
| `androidx.core:core-splashscreen` | **1.1.0-alpha02** | **1.2.0** (2025-11-05) | 0 | Có | Đang ở alpha. Splash là màn nhìn thấy được — phải so pixel | https://developer.android.com/jetpack/androidx/releases/core |
| `androidx.hilt:hilt-navigation-compose` | 1.2.0 | 1.4.0 (2026-07-01) | 0 | Có | Thấp. Kéo theo Navigation | https://developer.android.com/jetpack/androidx/releases/hilt |
| `androidx.navigation:navigation-compose` | **không khai báo trực tiếp** (transitive qua Hilt Navigation) | 2.10.1 (2026-09-09) | không biết chính xác bản đang resolve | Có | Nên khai báo trực tiếp, lấy version từ catalog. Không nhảy version mù | https://developer.android.com/jetpack/androidx/releases/navigation |
| `androidx.camera:camera-*` | **1.4.0-alpha04** (core, camera2, lifecycle, video, view, mlkit-vision, extensions) | **1.6.2** (2026-08-26). 1.7.0-alpha03 bỏ | 0, nhưng alpha → stable | Có | **Cao.** `camposer` bọc CameraX ở 14 file, nặng nhất `CameraState.kt`. minSdk library 23. `camera-mlkit-vision` có vẻ không dùng trong UI | https://developer.android.com/jetpack/androidx/releases/camera |
| `androidx.work:work-runtime` | 2.9.0 | 2.11.2 (2026-03-25). 2.12.0-rc01 bỏ | 0 | Có | **Không có call site.** Dependency chết | https://developer.android.com/jetpack/androidx/releases/work |
| `androidx.security:security-crypto` | **1.1.0-alpha06** | **1.1.0** (2025-07-30) | 0 | Có, nhịp chậm | API `MasterKey` + `EncryptedSharedPreferences` app đang dùng vẫn là API 1.1.0. Đang pin alpha | https://developer.android.com/jetpack/androidx/releases/security |
| `com.android.billingclient:billing` | 6.2.0 | **9.1.0** (2026-06-18) | **3** | Có | **Cao, bắt buộc sửa code.** Xem mục 4 | https://developer.android.com/google/play/billing/release-notes và https://developer.android.com/google/play/billing/migrate-gpblv9 |
| `com.google.android.gms:play-services-ads` | 23.0.0 | 25.5.0 (2026-09-17) | **2** | Có | Có. App open / interstitial / banner. UMP đi kèm | https://developers.google.com/admob/android/rel-notes |
| `com.google.android.ump:user-messaging-platform` | không khai báo trực tiếp; code import `com.google.android.ump.*` | 4.0.0 (2025-10-30) | — | Có | Đang resolve transitive từ Ads. Nên khai báo tường minh khi lên Ads 25 | https://developers.google.com/admob/android/privacy |
| `com.google.firebase:firebase-analytics` | 22.0.0 (không BOM) | BOM **34.19.0** (2026-09-09) kéo analytics **23.2.0** | 1 | Có | Thấp nếu chỉ `FirebaseAnalytics` mặc định | https://firebase.google.com/support/release-notes/android |
| `com.google.firebase:firebase-firestore` | 25.0.0 (không BOM) | cùng BOM → firestore **26.6.0** | 1 | Có | Trung bình. Phải đọc changelog 25 → 26 trước khi đụng call site | cùng link |

### Third-party

| group:artifact | Hiện tại | Stable mới nhất | Major | Maintain | Breaking | Release notes |
| --- | --- | --- | --- | --- | --- | --- |
| `com.jakewharton.timber:timber` | 5.0.1 | **5.0.1** (2021-08-13) | 0 | Không release mới. API ổn, chưa abandon theo nghĩa vỡ | Không | https://github.com/JakeWharton/timber/releases/tag/5.0.1 |
| `com.google.code.gson:gson` | 2.10.1 | 2.14.0 (2026-04-23) | 0 | Có | Thấp | https://github.com/google/gson/releases |
| `io.coil-kt:coil-compose` + `coil-video` | 2.5.0 | Dòng 2: **2.7.0** (2024-07-17). Dòng 3: `io.coil-kt.coil3:coil-compose:3.6.3` | 0 nếu ở lại dòng 2; **1 nếu sang Coil 3** | Dòng 2 đã dừng. Dòng 3 đang maintain | Coil 3 đổi package và API. **Có thể đổi cách decode/placeholder.** Quyết định ở Phase 1, không tự nhảy | https://github.com/coil-kt/coil/releases |
| `com.google.accompanist:accompanist-permissions` | 0.34.0 | 0.37.3 (2025-04-28) | 0 | Maintenance only, không feature mới | Thấp nếu chỉ bump. Thay bằng `rememberLauncherForActivityResult` là đổi code, không đổi dialog hệ thống | https://developer.android.com/develop/ui/compose/libraries và https://medium.com/androiddevelopers/an-update-on-jetpack-compose-accompanist-libraries-august-2023-ac4cbbf059f1 |
| `com.airbnb.android:epoxy` + `epoxy-processor` (kapt) | 5.1.4 | 5.2.1 (2026-01-23) | 0 | Có, nhịp chậm. 5.2 thêm KSP2 | Thấp với model hiện tại. KSP được hỗ trợ từ 5.2.0; Data Binding vẫn cần kapt — repo không dùng Data Binding của Epoxy | https://github.com/airbnb/epoxy/releases/tag/5.2.1 |
| `com.github.QuadFlask:colorpicker` | 0.0.15 (JitPack) | **Không có bản mới.** Tag 0.0.15 tồn tại (commit `90e47cd`). JitPack trả `modules: []`, message `Not found` | — | **Abandon** | Không được thay widget — đó là UI color picker ở Brightness | https://github.com/QuadFlask/colorpicker |
| `com.github.hadilq.liveevent:liveevent` | 1.2.0 (JitPack) | Không verify được artifact. JitPack `status: none` | — | **Abandon / không resolve sạch** | **Không có import trong source.** `LangViewHelper` dùng `MutableLiveData` | repo gốc historically https://github.com/hadilq/LiveEvent |

### Test

| group:artifact | Hiện tại | Stable mới nhất | Major | Ghi chú |
| --- | --- | --- | --- | --- |
| `junit:junit` | 4.13.2 | 4.13.2 | 0 | Đã latest dòng JUnit 4. JUnit 5 là thư viện khác — không tự đổi |
| `androidx.test.ext:junit` | 1.1.5 | 1.3.0 (2025-07-30) | 0 | https://developer.android.com/jetpack/androidx/releases/test |
| `androidx.test.espresso:espresso-core` | 3.5.1 | 3.7.0 (2025-07-30) | 0 | cùng link |
| `androidx.compose.ui:ui-test-junit4` | BOM 2023.08.00 | theo BOM 2026.09.00 | — | Test mẫu, không assertion UI thật |

---

## 4. Cờ đỏ

Có:

- **kapt** ở `:app` (Hilt + `lifecycle-compiler`) và `:languagepicker` (Epoxy). Không có kapt nào “bí” ngoài 3 processor này.
- **Compose compiler extension cũ** (`composeOptions.kotlinCompilerExtensionVersion`).
- **Java 8 bytecode** trong khi AGP 9 chạy trên JDK 17.
- **Alpha đang ship:** CameraX `1.4.0-alpha04`, SplashScreen `1.1.0-alpha02`, security-crypto `1.1.0-alpha06`.
- **Billing 6.2.0 → 9.1.0** không phải bump version. `BillingHelper.kt` còn:
  - `enablePendingPurchases()` không tham số (từ PBL 7 phải là `PendingPurchasesParams`, và bật one-time products)
  - `queryProductDetailsAsync` callback `(BillingResult, List)` (PBL 8 đổi thành `QueryProductDetailsResult`)
  - `queryPurchaseHistoryAsync` (bị gỡ ở PBL 8)
  - `BillingResponseCode.SERVICE_TIMEOUT` (đã bỏ ở các bản sau)
  - PBL 9 cần `compileSdk >= 35`
  - Guide chính thức chỉ cover 7/8 → 9. Từ 6 phải đi qua 7 rồi 8: https://developer.android.com/google/play/billing/migrate-gpblv9 và https://developer.android.com/google/play/billing/deprecation-faq
- **JitPack** còn trong `settings.gradle.kts` chỉ vì color picker và LiveEvent.
- **LiveEvent** khai báo nhưng không dùng.
- **WorkManager** khai báo nhưng không dùng.
- **Navigation Compose** không có dòng `implementation` trực tiếp.
- **Lifecycle khai báo hai version** trong cùng `app/build.gradle.kts`.
- **Firebase không BOM**, hai artifact pin tay lệch nhịp.
- **`camera-mlkit-vision`** kéo ML Kit dù không thấy call site analyzer trong app.
- **`package=` trong manifest** của `billing-utils` trong khi `namespace` đã set.
- **google-services** còn ở `buildscript.classpath`.
- **R8 tắt** (`isMinifyEnabled = false`). Proguard file là template, gần như không có rule thật.
- **Ad unit id production** nằm thẳng trong `app/build.gradle.kts` (`resValue`). Không đụng khi nâng toolchain. Không commit thêm bí mật.

Không có (đã grep): `jcenter()`, `android.enableJetifier`, `com.android.support`, Butter Knife, kotlin-android-extensions, AsyncTask, RxJava, Volley, GCM, Fabric, multidex thủ công.

---

## 5. Rủi ro build

- Không product flavor, không signing config riêng, không dynamic feature, không `applicationVariants` / `androidComponents`, không task Gradle tự viết.
- `buildConfig = true` và `BuildConfig.DEBUG` trong `MyApplication`. AGP 8+ đã tắt BuildConfig mặc định; repo bật lại. AGP 9 giữ được nếu cờ này còn. Phải giữ, không xóa.
- `resValue` ad id theo build type. Không đổi.
- `packaging { resources { excludes } }` đã là DSL mới. Không còn `packagingOptions`.
- Ba module library apply `com.android.library` không version.
- `rootProject.extra` cho billing và appcompat. Sẽ gộp vào version catalog ở commit upgrade, không phải refactor nghiệp vụ.
- AGP 9 bật **built-in Kotlin**. Apply thêm `org.jetbrains.kotlin.android` có thể đụng nhau. Opt-out `android.builtInKotlin=false` còn tới AGP 10. Repo không có code Variant API cũ nên opt-out DSL nhiều khả năng không cần — xác nhận lúc build, không đoán trước.
- Epoxy 5.2 processor yêu cầu Java 17 ở phía processor (theo PR migrate KSP của Epoxy). `jvmTarget = 1.8` của module có thể phải tách khỏi JDK chạy compiler.
- Không script lẫn Groovy/KTS.

---

## 6. Lưới an toàn

| Lớp | Có gì |
| --- | --- |
| Unit test | 1 test mẫu `ExampleUnitTest.addition_isCorrect` |
| Instrumentation | 1 test mẫu kiểm tra package name |
| Screenshot / coverage / lint baseline | Không |
| `lint.abortOnError` | `false`. `ContentDescription` bị disable. `checkDependencies = true` |
| R8 / minify | Tắt cả debug và release |

**Kết luận:** không có lưới an toàn nghiệp vụ. Mọi regression UI, billing, ads, camera, torch, compass chỉ bắt được bằng build + smoke test tay. Không refactor “tiện tay”. Mỗi bước upgrade phải build được trước khi đụng behaviour.

---

## 7. Mặt cắt runtime (để test sau)

Áp dụng cho checklist targetSdk 34 → 37. “Đụng repo” = có code hoặc manifest liên quan, chưa kết luận là sẽ crash.

| Hạng mục | Đụng repo này? | Chỗ |
| --- | --- | --- |
| Permission runtime | Có | `CameraPermission`: `CAMERA`, `READ_MEDIA_IMAGES` (API 33+), `READ_EXTERNAL_STORAGE` (dưới 33), `WRITE_EXTERNAL_STORAGE` (dưới 29). Manifest camposer còn `READ_MEDIA_AUDIO/VIDEO` dù app không xin lúc chạy |
| Foreground service type | Không | Không service của mình. Chỉ `AppLocalesMetadataHolderService` của AppCompat |
| Exact alarm / JobScheduler | Không | WorkManager không được gọi |
| Notification / custom view / widget / RemoteViews | Không | “StreamNotificationListener” trong Morse là callback nội bộ, không phải Notification |
| PendingIntent | Không thấy trong source app | |
| Deeplink | Không | Chỉ `MAIN` / `LAUNCHER` trên `SplashActivity` |
| SplashScreen | Có | `SplashActivity.installSplashScreen()`, theme `Theme.App.Starting` |
| Predictive back | Không có `OnBackPressed` riêng. Back do Navigation Compose | Đã target 34 nên một phần predictive back **có thể đã bật**. Nâng tiếp không phải lần đầu bật cờ |
| Edge-to-edge | **Chưa.** Theme set `statusBarColor` / `navigationBarColor`, không `enableEdgeToEdge` | Bắt buộc khi target 35, không opt-out được từ target 36. **R1:** bù inset, không thiết kế lại |
| Orientation / resizability | **Không** `screenOrientation`, `resizeableActivity`, `maxAspectRatio` | Cả `MainActivity` và `SplashActivity` khai báo `configChanges="orientation\|screenSize\|screenLayout\|keyboardHidden"` nên xoay **không recreate**. API 37 gỡ opt-out trên sw > 600dp — repo không opt-out, nhưng layout Compose không adaptive. Phải test tablet/foldable trước khi sửa. **Dừng hỏi trước khi đụng** |
| IME | Có | Morse `BasicTextField`; language dialog `EditText` `inputType=text` (không phải password). `windowSoftInputMode=adjustResize`. API 17 (mọi app): IME không tự hiện lại sau xoay nếu app không tự xử lý |
| Storage / scoped storage | Có | `requestLegacyExternalStorage` (bị bỏ qua từ target 30, app đã target 34), `maxSdkVersion` trên storage permission, gallery đọc ảnh |
| Camera / torch | Có | Home + Morse: `CameraManager.setTorchMode`. Camera screen: CameraX qua `:camposer`. Manifest có `CAMERA` |
| Audio | Có | `MediaPlayer` tiếng bật/tắt đèn; `AudioTrack` Morse. API 37 siết audio nền — phải xem Morse có chạy khi app vào background |
| Sensor | Có | Compass: accelerometer + magnetometer |
| Ads | Có | Banner, interstitial, app open. App open show ở `ON_START`. API 37 mở rộng chặn Background Activity Launch sang `IntentSender` — rủi ro nằm ở App Open Ad |
| Consent / UMP | Có | `GoogleMobileAdsConsentManager` |
| Billing | Có | `:billing-utils`, màn Pro |
| Firebase | Có | Analytics + Firestore. Không thấy messaging |
| Biometric / Bluetooth / WebView / SMS OTP | Không | |
| Password field | Không | Không `inputType=password` |
| LAN / mDNS / NSD / cast / printer | Không thấy trong source | Có `ACCESS_WIFI_STATE` + `INTERNET` + `ACCESS_NETWORK_STATE`. Permission LAN mới của API 37 **chưa thấy call site**. Vẫn soi merged manifest sau khi lên Ads/Firebase |
| Native `.so` / NDK | Không có source native | 16KB page size (API 36) vẫn áp vào `.so` của Ads, Firebase, CameraX, Tink (security-crypto). Phải kiểm APK ở baseline và sau upgrade |
| Reflection / MessageQueue / static final | Không | `Handler(Looper.getMainLooper())` là API public. Không `setAccessible`, không `DexClassLoader`, không `System.loadLibrary` |
| Dynamic code loading | Không | |
| Network security / cleartext / ECH | Không có `networkSecurityConfig`, không `usesCleartextTraffic` | ECH mặc định trên Android 17 ảnh hưởng nếu có MITM nội bộ. App không pin SNI |
| Keystore | Có | `EncryptedSharedPreferences` một master key. Hạn mức key Android 17 (50k) không phải vấn đề của app này |
| LeakCanary | Không | Nên chỉ đề xuất, không tự thêm ở đợt này |

Permission manifest hợp nhất (khai báo, chưa phải merged binary): `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `AD_ID`, `BILLING`, `CAMERA`, `READ_MEDIA_IMAGES`, `READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO`, `READ_EXTERNAL_STORAGE` (max 32), `WRITE_EXTERNAL_STORAGE` (max 29).

---

## 8. Việc Phase 0 cố ý không làm

- Không tạo branch, không tag, không commit.
- Không chạy `./gradlew` (baseline thuộc Phase 2, sau khi plan được duyệt).
- Không chọn hộ các ngã ba Coil 3, kapt→KSP, Material3 defaults, Billing 9. Những cái đó là nội dung Phase 1.
