import com.dev.innerview.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidMedia3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("media3.exoplayer").get())
                add("implementation", libs.findLibrary("media3.exoplayer.dash").get())
                add("implementation", libs.findLibrary("media3.exoplayer.hls").get())
                add("implementation", libs.findLibrary("media3.player.session").get())
                add("implementation", libs.findLibrary("media3.ui").get())
            }
        }
    }
}