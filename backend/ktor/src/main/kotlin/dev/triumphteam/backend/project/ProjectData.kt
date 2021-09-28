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