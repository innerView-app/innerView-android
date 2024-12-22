plugins {
    id("innerview.android.feature")
}

android {
    namespace = "com.dev.innerview.feature.main"
}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:peek"))
    implementation(project(":feature:edit"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:record"))
    implementation(project(":feature:notification"))
}