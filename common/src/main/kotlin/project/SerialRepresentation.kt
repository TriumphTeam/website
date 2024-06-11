package dev.triumphteam.website.project

import kotlinx.serialization.Serializable

@Serializable
public data class Repository(
    public val editPath: String,
    public val projects: List<Project>,
)

@Serializable
public data class Project(
    public val id: String,
    public val name: String,
    public val icon: String,
    public val projectHome: String,
    public val versions: List<ProjectVersion>,
)

@Serializable
public data class ProjectVersion(
    public val ref: String,
    public val status: VersionStatus,
    public val groups: List<PageGroup>,
)

@Serializable
public data class PageGroup(
    public val header: String,
    public val pages: List<Page>,
)

@Serializable
public data class Page(
    public val id: String,
    public val html: String,
    public val content: PageContent,
)

@Serializable
public data class ContentEntry(val literal: String, val href: String, val indent: UInt)

@Serializable
public data class PageContent(val path: String, val entries: List<ContentEntry>)

@Serializable
public enum class VersionStatus {

    ALPHA,
    STABLE,
    SNAPSHOT,
}
