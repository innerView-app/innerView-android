plugins {
    id("innerview.android.library")
    id("innerview.android.kotlin.serialization")
}

android {
    namespace = "com.dev.innerview.core.rendering"
}

dependencies {
    implementation(files("libs/ffmpeg-kit-full-6.0-2.aar"))
    implementation(libs.smart.exception.java)

    implementation(project(":core:model"))
    implementation(project(":core:notification"))
    implementation(project(":core:domain"))
}