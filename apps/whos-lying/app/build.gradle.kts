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

android {
    namespace = "com.jorgelillo.whoslying"

    defaultConfig {
        applicationId = "com.jorgelillo.whoslying"
        minSdk = 24 // navigation-compose needs API 24
        versionCode = 3
        versionName = "1.0"
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
    implementation(projects.apps.whosLying.domain)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
}
