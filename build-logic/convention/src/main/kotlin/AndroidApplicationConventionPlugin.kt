import com.dev.innerview.configureComposeAndroid
import com.dev.innerview.configureKotlinAndroid
import com.dev.innerview.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("innerview.android.hilt")
            }

            configureKotlinAndroid()
            configureComposeAndroid()

            dependencies {
                add("implementation", libs.findLibrary("androidx.activity.compose").get())
            }
        }
    }
}