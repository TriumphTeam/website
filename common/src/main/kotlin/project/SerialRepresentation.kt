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
    public val color: String,
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
    public val path: String,
    public val description: Description,
    public val default: Boolean,
) {

    @Serializable
    public data class Banner(
        public val title: String?,
        public val group: String,
        public val subTitle: String?,
    )

    @Serializable
    public data class Description(
        public val title: String?,
        public val group: String,
        public val subTitle: String?,
        public val summary: List<Summary>,
    )

    @Serializable
    public data class Summary(
        public val literal: String,
        public val href: String,
        public val terms: List<String>,
        public val children: List<Summary>,
    )
}
