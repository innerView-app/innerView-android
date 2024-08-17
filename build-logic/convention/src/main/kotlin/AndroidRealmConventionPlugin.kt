import com.dev.innerview.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRealmConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.realm.kotlin")
            }

            dependencies {
                add("implementation", libs.findLibrary("realm.library.base").get())
                add("implementation", libs.findLibrary("realm.library.sync").get())
            }
        }
    }
}