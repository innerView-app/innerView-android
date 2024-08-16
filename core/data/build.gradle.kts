plugins {
    id("innerview.android.library")
    id("innerview.android.kotlin.serialization")
    id("innerview.android.realm")
}

android {
    namespace = "com.dev.innerview.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data-api"))

    implementation(project(":core:database"))
}