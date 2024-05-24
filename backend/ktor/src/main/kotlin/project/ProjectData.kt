package dev.triumphteam.backend.project

import kotlinx.serialization.Serializable

@Serializable
data class ProjectInfo(
    val name: String,
    val color: GradientColor,
)

@JvmInline
@Serializable
value class GradientColor(val colors: List<String>)
