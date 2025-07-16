package dev.triumphteam.website.serializable

import kotlinx.serialization.Serializable

@Serializable
public data class VersionDocument(
    public val versions: List<Version>,
    public val color: String,
    public val stable: Boolean,
    public val groups: List<Group>,
    public val platforms: List<String>,
    public val languages: List<String>,
    public val buildTools: List<String>,
    public val github: String?,
    public val discord: String?,
    public val javadocs: String?,
) {

    @Serializable
    public data class Version(public val reference: String)

    @Serializable
    public data class Group(public val name: String, public val pages: List<Page>)

    @Serializable
    public data class Page(public val id: String, public val name: String)
}

@Serializable
public data class PageDocument(
    public val name: String,
    public val description: String,
    public val content: DocComponent,
    public val previous: Navigation?,
    public val next: Navigation?,
    public val sections: List<PageContent>,
) {

    @Serializable
    public data class PageContent(
        public val id: String,
        public val name: String,
        public val level: Int,
    )

    @Serializable
    public data class Navigation(public val id: String, public val name: String)
}
