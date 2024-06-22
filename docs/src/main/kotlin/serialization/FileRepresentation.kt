package dev.triumphteam.website.docs.serialization

import dev.triumphteam.website.project.Navigation
import kotlinx.serialization.Serializable

@Serializable
public data class RepoSettings(public val editPath: String)

@Serializable
public data class ProjectConfig(
    public val id: String,
    public val name: String,
    public val icon: String,
    public val color: String,
    public val projectHome: String,
)

@Serializable
public data class VersionConfig(
    public val reference: String,
    public val recommended: Boolean = false,
    public val stable: Boolean = true,
)

@Serializable
public data class GroupConfig(public val header: String, public val pages: List<PageConfig>) {

    public fun mapPages(): List<Navigation.Page> {
        return pages.map { Navigation.Page(it.header, it.link) }
    }
}

@Serializable
public data class PageConfig(public val header: String, public val link: String)
