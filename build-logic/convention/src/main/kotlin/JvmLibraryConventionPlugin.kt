import com.jorgelillo.buildlogic.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Pure Kotlin module with no Android dependencies (domain logic). */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        configureKotlinJvm()
    }
}
