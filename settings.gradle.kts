pluginManagement {
    repositories {
        google()
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

rootProject.name = "LifeLinkAI"

include(":app")
include(":core:common")
include(":core:ui")
include(":core:ai")
include(":data:local")
include(":data:remote")
include(":data:repository")
include(":feature:onboarding")
include(":feature:sos")
include(":feature:medical")
include(":feature:guides")
include(":feature:assistant")
include(":feature:hospital")
