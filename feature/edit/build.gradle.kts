plugins {
    id("innerview.android.feature")
    id("innerview.android.media3")
}

android {
    namespace = "com.dev.innerview.feature.edit"
}

dependencies {
    implementation(project(":core:playback"))
}