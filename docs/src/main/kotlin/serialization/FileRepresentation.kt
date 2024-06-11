package dev.triumphteam.website.docs.serialization

import dev.triumphteam.website.project.VersionStatus
import kotlinx.serialization.Serializable

@Serializable
public data class RepoSettings(public val editPath: String)

@Serializable
public data class ProjectConfig(
    public val id: String,
    public val name: String,
    public val icon: String,
    public val projectHome: String,
)

@Serializable
public data class VersionConfig(public val ref: String, public val status: VersionStatus)

@Serializable
public data class GroupConfig(public val header: String)
