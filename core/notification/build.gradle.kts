plugins {
    id("innerview.android.library")
}

android {
    namespace = "com.dev.innerview.core.notification"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:data-api"))
    implementation(project(":core:navigation"))
}