plugins {
    id("innerview.android.library")
}

android {
    namespace = "com.dev.innerview.core.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:data-api"))
    implementation(project(":core:notification"))
}