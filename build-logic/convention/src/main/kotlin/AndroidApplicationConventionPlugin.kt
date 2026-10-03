import com.android.build.api.dsl.ApplicationExtension
import com.jorgelillo.buildlogic.configureKotlinAndroid
import com.jorgelillo.buildlogic.intVersion
import com.jorgelillo.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("org.jetbrains.kotlin.android")
        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = libs.intVersion("targetSdk")
        }
    }
}
