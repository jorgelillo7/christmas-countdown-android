import com.android.build.api.dsl.ApplicationExtension
import com.jorgelillo.buildlogic.configureKotlinAndroid
import com.jorgelillo.buildlogic.intVersion
import com.jorgelillo.buildlogic.libs
import java.util.Properties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Every app: SDK levels, Kotlin, and a release build that is shrunk with R8 and signed with the
 * app's upload key from the git-ignored `apps/<app>/keystore.properties`. Without that file the
 * release build is signed with the debug key, so the R8 build can still be smoke tested (Play
 * rejects debug-signed bundles).
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        val keystoreFile = file("../keystore.properties")
        val keystore = Properties().apply { if (keystoreFile.exists()) keystoreFile.inputStream().use { load(it) } }
        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = libs.intVersion("targetSdk")
            signingConfigs.create("release") {
                if (keystoreFile.exists()) {
                    storeFile = file(keystore.getProperty("storeFile").trim())
                    storePassword = keystore.getProperty("storePassword")?.trim()
                    keyAlias = keystore.getProperty("keyAlias")?.trim()
                    keyPassword = keystore.getProperty("keyPassword")?.trim()
                }
            }
            buildTypes.getByName("release") {
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                signingConfig = signingConfigs.getByName(if (keystoreFile.exists()) "release" else "debug")
            }
        }
    }
}
