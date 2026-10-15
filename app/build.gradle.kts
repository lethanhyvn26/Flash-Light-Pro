plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "flashlight.phichung.com.torch"
    compileSdk = 37

    defaultConfig {
        applicationId = "flashlight.phichung.com.torch"
        minSdk = 24
        targetSdk = 37
        versionCode = 20
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val releaseStorePassword = "huutriphichung"
    val releaseKeyAlias = "flashlight"
    val releaseKeyPassword = "huutriphichung"

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("FlashlightPro.jks")
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }

    buildTypes {
        release {
            if (
                releaseStorePassword.isNotBlank() &&
                releaseKeyAlias.isNotBlank() &&
                releaseKeyPassword.isNotBlank()
            ) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "str_ads_google_app_id", "ca-app-pub-4796190056177032~2575781118")
            resValue("string", "str_ads_banner", "ca-app-pub-4796190056177032/1934537304")
            resValue("string", "str_ads_open", "ca-app-pub-4796190056177032/5682210625")
            resValue("string", "str_ads_interstitial", "ca-app-pub-4796190056177032/4008282285")
        }
        debug {
            isMinifyEnabled = false
            resValue("string", "str_ads_google_app_id", "ca-app-pub-3940256099942544~3347511713")
            resValue("string", "str_ads_banner", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "str_ads_open", "ca-app-pub-3940256099942544/9257395921")
            resValue("string", "str_ads_interstitial", "ca-app-pub-3940256099942544/1033173712")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
        // AGP 9 disables build-type resValue unless this is on. Ad unit ids live there.
        resValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        disable += "ContentDescription"
        abortOnError = false
        checkDependencies = true
        checkReleaseBuilds = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.billing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.colorpicker)
    implementation(libs.gson)
    implementation(project(":camposer"))
    implementation(project(":billing-utils"))
    implementation(project(":languagepicker"))
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.ads)
    implementation(libs.androidx.security.crypto)
}
