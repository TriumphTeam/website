import org.gradle.internal.impldep.org.bouncycastle.asn1.x500.style.RFC4519Style.name

dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "website"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("backend")
include("docs")
include("common")

listOf(
    "scripts/base",
    "scripts/observer",
).forEach(::includeProjectFolders)


fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}

fun includeProjectFolders(path: String) {
    val (_, name) = path.split('/')
    include(name) {
        this.name = "${rootProject.name}-$name"
        this.projectDir = file(path)
    }
}
