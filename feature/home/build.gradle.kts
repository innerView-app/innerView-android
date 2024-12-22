plugins {
    id("innerview.android.feature")
}

android {
    namespace = "com.dev.innerview.feature.home"
}

dependencies {
    implementation(project(":feature:notification"))
}