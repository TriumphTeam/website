package dev.triumphteam.website.docs.project

import kotlinx.serialization.Serializable

@Serializable
public data class RepoSettings(public val editPath: String)

@Serializable
public data class ProjectConfig(
    public val id: String,
    public val name: String,
    public val color: String,
    public val projectHome: String,
    public val discord: String?,
)

@Serializable
public data class VersionConfig(
    public val reference: String,
    public val recommended: Boolean = false,
    public val stable: Boolean = true,
    public val github: String? = null,
    public val discord: String? = null,
    public val javadocs: String? = null,
)

@Serializable
public data class GroupConfig(public val name: String, public val order: Int)

@Serializable
public data class PageConfig(
    public val id: String,
    public val name: String,
    public val description: String,
    public val order: Int,
)

/*@Serializable
public data class ConditionalPlaceholders(

)*/
