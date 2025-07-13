package dev.triumphteam.website.project

import dev.triumphteam.website.docs.DocComponent
import kotlinx.serialization.Serializable

@Serializable
public data class Repository(public val projects: List<Project>)

@Serializable
public data class Project(
    public val id: String,
    public val name: String,
    public val color: String,
    public val versions: List<Version>,
)

@Serializable
public data class Version(
    public val reference: String,
    public val recommended: Boolean,
    public val stable: Boolean,
    public val groups: List<Group>,
    public val platforms: List<String>,
    public val languages: List<String>,
    public val buildTools: List<String>,
    public val github: String?,
    public val discord: String?,
    public val javadocs: String?,
)

@Serializable
public data class Group(public val name: String, public val order: Int, public val pages: List<Page>)

@Serializable
public data class Page(
    public val id: String,
    public val path: String,
    public val name: String,
    public val order: Int,
    public val description: String,
    public val content: DocComponent,
)
