plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")

}

android {
    namespace = "com.haohao.languagepicker"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding =  true

    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


}

val appcompatX = rootProject.extra.get("appcompatX") as String

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:$appcompatX")

    implementation("com.google.android.material:material:1.11.0")


    // language picker
    val epoxyVersion = "5.1.4"
    implementation ("com.airbnb.android:epoxy:$epoxyVersion")
    // Add the annotation processor if you are using Epoxy's annotations (recommended)
    kapt ("com.airbnb.android:epoxy-processor:$epoxyVersion")
    // annotationProcessor "com.airbnb.android:epoxy-processor:$epoxyVersion"
    implementation("com.github.hadilq.liveevent:liveevent:1.2.0")
}