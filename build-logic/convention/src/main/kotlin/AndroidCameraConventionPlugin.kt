import com.dev.innerview.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidCameraConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("camera.core").get())
                add("implementation", libs.findLibrary("camera.camera2").get())
                add("implementation", libs.findLibrary("camera.lifecycle").get())
                add("implementation", libs.findLibrary("camera.video").get())
                add("implementation", libs.findLibrary("camera.view").get())
                add("implementation", libs.findLibrary("camera.extensions").get())
            }
        }
    }
}