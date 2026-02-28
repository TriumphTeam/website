package dev.triumphteam.website.api

import dev.triumphteam.website.serializable.PAGE_ROUTE
import dev.triumphteam.website.serializable.PROJECTS_ROUTE
import dev.triumphteam.website.serializable.PROJECT_ROUTE
import dev.triumphteam.website.serializable.SEARCH_DATA_ROUTE
import io.ktor.resources.Resource

public object Api {

    @Resource(PROJECT_ROUTE)
    public data class Project(
        public val version: String? = null,
        public val project: String,
    )

    @Resource(PROJECTS_ROUTE)
    public data object Projects

    @Resource(PAGE_ROUTE)
    public data class Page(
        public val version: Int,
        public val page: String,
    )

    @Resource(SEARCH_DATA_ROUTE)
    public data class SearchData(public val version: Int)
}
