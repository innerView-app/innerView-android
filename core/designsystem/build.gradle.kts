plugins {
    id("innerview.android.library")
    id("innerview.android.compose")
}

android {
    namespace = "com.dev.innerview.core.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
}