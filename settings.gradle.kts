pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "innerView"
include(":app")

include(":feature:main")
include(":feature:home")
include(":feature:peek")
include(":feature:edit")
include(":feature:profile")

include(":core:designsystem")
include(":core:navigation")
include(":core:database")
include(":core:model")
include(":core:data")
include(":core:data-api")
include(":core:domain")
include(":feature:record")
include(":feature:notification")
