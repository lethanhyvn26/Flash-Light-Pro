# 01 — Plan

Ngày: 2026-09-23. Inventory: `00-inventory.md`.
Quyết định dưới đây khóa cho đợt này. UI/UX người dùng nhìn thấy giữ nguyên.

## 1. Target

| | Giữ / chọn | Lý do |
| --- | --- | --- |
| compileSdk | **37** | AGP 9.4.1 hỗ trợ tối đa API 37 |
| targetSdk | **34 → 35 → 36 → 37**, mỗi nấc một commit | Tách behavior change khỏi lỗi toolchain |
| minSdk app | **24** | Không cắt user |
| minSdk library | **24** | App đã 24. CameraX 1.6 yêu cầu ≥ 23. Module không publish riêng |

## 2. Version khóa

Verify Maven + release notes ngày 2026-09-23.

| Thành phần | Chọn | Vì sao không phải bản khác |
| --- | --- | --- |
| Gradle | **9.7.1** (2026-08-19) | Stable mới nhất. AGP 9.4 tối thiểu 9.6.0. Nếu AGP/Kotlin cảnh báo chặn build, tụt về 9.6.0 |
| AGP | **9.4.1** (2026-09-18) | Stable mới nhất. 9.5.0 chỉ có alpha |
| Kotlin | **2.4.20** (2026-09-07) | Stable mới nhất |
| KSP | **2.3.12** | Stable mới nhất trên Maven. Build sẽ xác nhận cặp với Kotlin 2.4.20 |
| JDK chạy Gradle | **17** (máy đang Temurin 17.0.19) | AGP 9.4 yêu cầu JDK 17 |
| jvmTarget | **17** | Khớp JDK toolchain. Không đổi UI |
| Compose BOM | **2026.09.00** | Stable mới nhất. Material3 1.4.0 |
| Compose compiler | plugin `org.jetbrains.kotlin.plugin.compose` | Bỏ `kotlinCompilerExtensionVersion` |
| Hilt | **2.60.1** + KSP (`hilt-compiler`) | Bỏ kapt |
| google-services | **4.5.0**, plugins DSL | Bỏ `buildscript.classpath` |
| Firebase BOM | **34.19.0** | analytics 23.2.0, firestore 26.6.0 |
| Billing | **9.1.0** | Play đã deprecate 6.x. Sửa API ở commit riêng |
| Ads | **25.5.0** | Stable mới nhất |
| CameraX | **1.6.2** | Bỏ alpha 1.4. Bỏ `camera-mlkit-vision` (không có call site) |
| core-ktx | 1.19.0 | |
| lifecycle | 2.11.0 | Bỏ alpha 2.12 |
| activity-compose | 1.13.0 | |
| appcompat | 1.8.0 | |
| material | 1.14.0 | |
| splashscreen | 1.2.0 | Bỏ alpha |
| navigation-compose | 2.10.1 | Khai báo trực tiếp (hiện chỉ transitive) |
| hilt-navigation-compose | 1.4.0 | |
| security-crypto | 1.1.0 | Bỏ alpha06 |
| work-runtime | **gỡ** | Không có call site |
| liveevent | **gỡ** | Không có import. JitPack không còn artifact sạch |
| Coil | **2.7.0** (dòng `io.coil-kt`) | Không sang Coil 3: đổi package, có thể đổi placeholder/decode |
| colorpicker | **0.0.15 pin** | Repo abandon. Widget là UI Brightness |
| accompanist-permissions | **0.37.3** | Không viết lại flow permission |
| Epoxy | **5.2.1** + KSP | 5.2 hỗ trợ KSP2 |
| Timber | 5.0.1 | Đã là bản cuối |
| Gson | 2.14.0 | |
| junit | 4.13.2 | Không nhảy JUnit 5 |
| espresso | 3.7.0 | |
| test ext junit | 1.3.0 | |

## 3. Thứ tự commit

1. `docs: add upgrade inventory and plan`
2. `chore(baseline): record pre-upgrade build, lint and APK metrics`
3. `chore(deps): upgrade toolchain and all dependencies to latest stable` — compileSdk 37, targetSdk giữ 34
4. `fix(billing): migrate Play Billing 6 to 9` — nếu 6.2 không compile trên 9.1
5. `fix(lifecycle): replace OnLifecycleEvent with DefaultLifecycleObserver` — bỏ kapt lifecycle-compiler
6. `fix(compose): adapt call sites to Material3 and Compose API changes` — chỉ API gãy (Divider → HorizontalDivider, v.v.), không đổi layout
7. Các `fix(...)` khác nếu build còn đỏ, mỗi nguyên nhân một commit
8. `feat(sdk): target Android 15 (API 35)` + inset để giữ visual parity
9. `feat(sdk): target Android 16 (API 36)`
10. `feat(sdk): target Android 17 (API 37)`
11. `chore(lint)` / `chore(kotlin)` / `chore(gradle)` dọn warning
12. `docs: add post-upgrade report`

Rollback mỗi commit: `git revert <hash>` trên nhánh này. Không rebase, không force.

## 4. Breaking change đã biết

| Thay đổi | Nơi | Cách sửa |
| --- | --- | --- |
| AGP 9 built-in Kotlin | mọi `build.gradle.kts` | Bỏ plugin Kotlin trùng nếu AGP báo conflict. Giữ plugin Compose + KSP |
| `kotlinCompilerExtensionVersion` | app, camposer | Plugin Compose |
| kapt Hilt / Epoxy | app, languagepicker | `ksp(...)` |
| `@OnLifecycleEvent` | `MyApplication` | `DefaultLifecycleObserver.onStart` |
| Billing 9 | `BillingHelper` | `PendingPurchasesParams`, `QueryProductDetailsResult`, bỏ `queryPurchaseHistoryAsync` và `SERVICE_TIMEOUT` |
| Material3 `Divider` | Settings | `HorizontalDivider` |
| CameraX alpha → 1.6.2 | `:camposer` | Sửa chữ ký nếu compile gãy. minSdk module 24 |
| Edge-to-edge khi target ≥ 35 | theme + scaffold | Bù inset một lần. Không thiết kế lại màn hình |
| `statusBarColor` deprecated | `Theme.kt` | Giữ màu qua window background `color_app` (đã trùng màu bar) |

## 5. Cố ý không làm

- Không Coil 3, không thay color picker, không viết lại permission UI.
- Không bật R8 (đang tắt — bật là đổi behavior release, ngoài phạm vi “không đổi UX” và không có keep rule thật).
- Không bật `warningsAsErrors` (spec bảo hỏi trước). Dọn warning, không khóa cổng.
- Không thêm LeakCanary, screenshot test, CI.
- API 37 resizability: repo không khóa orientation / `resizeableActivity`. Không sửa manifest. Ghi vào report để smoke test tablet.

## 6. Verify

```
./gradlew assembleDebug testDebugUnitTest lintDebug
```

Smoke test tay (không có test nghiệp vụ):

- Splash → Home, bật/tắt đèn, âm thanh
- Settings, đổi ngôn ngữ, privacy ads
- Brightness + color picker
- Compass
- Morse (gõ, phát, thoát app giữa chừng)
- Camera → Gallery → Preview
- Skin
- Pro / billing (cần Play, có thể không chạy trên emulator không có store)
- Xoay máy, gesture nav và 3-button

API level: 24 (min), 34 (target cũ), 37 (target mới). Một máy sw ≥ 600dp.
