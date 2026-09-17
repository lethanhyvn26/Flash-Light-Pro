plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}




android {
    namespace = "com.haohao.billing"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


}


val billingLibraryVersion = rootProject.extra.get("billingLibraryVersion") as String
val appcompatX = rootProject.extra.get("appcompatX") as String


dependencies {
    implementation("androidx.appcompat:appcompat:$appcompatX")
    implementation("com.android.billingclient:billing:$billingLibraryVersion")

}
