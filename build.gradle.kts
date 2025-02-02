import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.realm.database) apply false
    alias(libs.plugins.google.services) apply false
}

apply {
    from("gradle/projectDependencyGraph.gradle")
}

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {

            // Trigger this with:
            // ./gradlew assembleRelease -PenableMultiModuleComposeReports=true --rerun-tasks
            // java -jar mendable.jar --scanPaths .\build\compose_metrics\

            if (project.findProperty("enableMultiModuleComposeReports") == "true") {
                val buildDirPath = rootProject.layout.buildDirectory.get().asFile.absolutePath
                freeCompilerArgs.addAll(
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$buildDirPath/compose_metrics/",
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$buildDirPath/compose_metrics/"
                    )
                )
            }
        }
    }
}