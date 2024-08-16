plugins {
    id("innerview.android.library")
    id("innerview.android.kotlin.serialization")
}

android {
    namespace = "com.dev.innerview.core.data_api"
}

dependencies {
    implementation(project(":core:model"))
}