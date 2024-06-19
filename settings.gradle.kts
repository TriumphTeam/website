dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "website"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("backend")
include("docs")
include("common")
include("script")


fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
