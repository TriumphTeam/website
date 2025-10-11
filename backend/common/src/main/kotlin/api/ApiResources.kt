package dev.triumphteam.website.api

import io.ktor.resources.Resource

public object Api {

    @Resource("/project")
    public data class Project(
        public val version: String? = null,
        public val project: String,
    )

    @Resource("/projects")
    public data object Projects

    @Resource("/page")
    public data class Page(
        public val version: Int,
        public val page: String,
    )

    @Resource("/search-data")
    public data class SearchData(public val version: Int)
}
