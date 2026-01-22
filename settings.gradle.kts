import dev.triumphteam.root.projects

dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.triumphteam.dev/releases")
    }
}

rootProject.name = "website"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("dev.triumphteam.root.settings") version "0.0.39"
}

projects {
    single(id = "common")
    single(id = "serializable")

    single(id = "docs")

    single(id = "backend")
    single(id = "frontend")
}
