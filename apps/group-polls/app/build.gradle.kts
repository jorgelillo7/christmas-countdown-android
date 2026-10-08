import java.util.Properties

plugins {
    alias(libs.plugins.jorgelillo.android.application)
    alias(libs.plugins.jorgelillo.android.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Release signing values live in a git-ignored keystore.properties next to this app's folder.
val keystorePropertiesFile = file("../keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) keystorePropertiesFile.inputStream().use { load(it) }
}

// Firebase client config (public identifiers, not secrets) from the Firebase console. Without the
// file the app runs on its in-memory backend.
val firebasePropertiesFile = file("../firebase.properties")
val firebaseProperties = Properties().apply {
    if (firebasePropertiesFile.exists()) firebasePropertiesFile.inputStream().use { load(it) }
}
fun firebase(key: String) = "\"" + firebaseProperties.getProperty(key, "").trim() + "\""

android {
    namespace = "com.jorgelillo.grouppolls"

    defaultConfig {
        applicationId = "com.jorgelillo.grouppolls"
        minSdk = 24 // navigation-compose needs API 24
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "FIREBASE_PROJECT_ID", firebase("projectId"))
        buildConfigField("String", "FIREBASE_APP_ID", firebase("appId"))
        buildConfigField("String", "FIREBASE_API_KEY", firebase("apiKey"))
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties.getProperty("storeFile").trim())
                storePassword = keystoreProperties.getProperty("storePassword")?.trim()
                keyAlias = keystoreProperties.getProperty("keyAlias")?.trim()
                keyPassword = keystoreProperties.getProperty("keyPassword")?.trim()
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Until the app has its upload key, sign release builds with the debug key so the
            // R8-shrunk build can be smoke tested. Play rejects debug-signed bundles.
            signingConfig = signingConfigs.getByName(if (keystorePropertiesFile.exists()) "release" else "debug")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    // Lists the app's languages so Android 13+ offers them in system settings and the in-app picker works.
    androidResources {
        generateLocaleConfig = true
    }
}

dependencies {
    implementation(projects.apps.groupPolls.domain)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
}
