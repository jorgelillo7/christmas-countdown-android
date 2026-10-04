pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "lillo-android-apps"

// Shared modules
include(":core:designsystem")

// Christmas Countdown
include(":apps:christmas-countdown:app")
include(":apps:christmas-countdown:domain")

// Decision wheel ("What next?")
include(":apps:decision-wheel:app")
include(":apps:decision-wheel:domain")
