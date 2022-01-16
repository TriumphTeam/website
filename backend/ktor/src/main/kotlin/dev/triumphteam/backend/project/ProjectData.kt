package dev.triumphteam.backend.project

import dev.triumphteam.backend.project.summary.Entry
import kotlinx.serialization.Serializable

@Serializable
data class ProjectData(
    val options: ProjectOptions,
    val summary: List<Entry>
)

@Serializable
data class ProjectOptions(
    val name: String,
    val color: List<String>,
    val github: String,
)

val String.projectType: UInt
    get() = when (this) {
        "library" -> 1u
        else -> 0u
    }

val UInt.projectType: String
    get() = when (this) {
        1u -> "library"
        else -> "plugin"
    }

fun String.toSingularProjectType() = when (this) {
    "libraries" -> "library"
    "plugins" -> "plugin"
    else -> null
}