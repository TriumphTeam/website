dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "website"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("backend")
include("docs")
include("common")
include("serializable")
include("scripting")
