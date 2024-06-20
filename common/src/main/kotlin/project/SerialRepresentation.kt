package dev.triumphteam.website.project

import kotlinx.serialization.Serializable

@Serializable
public data class Repository(
    public val projects: List<Project>,
)

@Serializable
public data class Project(
    public val id: String,
    public val name: String,
    public val icon: String,
    public val projectHome: String,
    public val versions: List<DocVersion>,
)

@Serializable
public data class DocVersion(
    public val reference: String,
    public val recommended: Boolean,
    public val stable: Boolean,
    public val navigation: Navigation,
    public val pages: List<Page>,
)

@Serializable
public data class Navigation(public val groups: List<Group>) {

    @Serializable
    public data class Group(public val header: String, public val pages: List<Page>)

    @Serializable
    public data class Page(public val header: String, public val id: String)
}

@Serializable
public data class Page(
    public val id: String,
    public val content: String,
    public val summary: PageSummary,
)

@Serializable
public data class ContentEntry(val literal: String, val href: String, val indent: UInt)

@Serializable
public data class PageSummary(val path: String, val entries: List<ContentEntry>)
