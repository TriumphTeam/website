package dev.triumphteam.website.serializable

import kotlinx.serialization.Serializable

@Serializable
public data class ProjectData(
    public val id: String,
    public val name: String,
    public val color: String,
    public val versions: List<VersionData>,
)

