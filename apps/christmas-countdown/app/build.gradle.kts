import java.util.Properties

plugins {
    alias(libs.plugins.jorgelillo.android.application)
    alias(libs.plugins.jorgelillo.android.compose)
}

// Release signing values live in a git-ignored keystore.properties next to this app's folder.
val keystorePropertiesFile = file("../keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) keystorePropertiesFile.inputStream().use { load(it) }
}

android {
    namespace = "com.jorgelillo.christmascountdown"

    defaultConfig {
        applicationId = "com.jorgelillo.christmascountdown"
        versionCode = 3
        versionName = "3.0"
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
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.apps.christmasCountdown.domain)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
}
