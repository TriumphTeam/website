dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "backend"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("ktor")
include("markdown")


fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
