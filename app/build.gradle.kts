plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val uploadKeystore = file("upload-key.jks")
val uploadKeyAlias = System.getenv("ANDROID_KEY_ALIAS")
val uploadStorePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
val uploadKeyPassword = System.getenv("ANDROID_KEY_PASSWORD")
val releaseSigningConfigured = uploadKeystore.exists() &&
    !uploadKeyAlias.isNullOrBlank() &&
    !uploadStorePassword.isNullOrBlank() &&
    !uploadKeyPassword.isNullOrBlank()

android {
    namespace = "com.aipulse.app"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.aipulse.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "0.3.0"
    }
    if (releaseSigningConfigured) {
        signingConfigs {
            create("release") {
                storeFile = uploadKeystore
                storePassword = uploadStorePassword
                keyAlias = uploadKeyAlias
                keyPassword = uploadKeyPassword
            }
        }
    }
    buildTypes {
        getByName("release") {
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
