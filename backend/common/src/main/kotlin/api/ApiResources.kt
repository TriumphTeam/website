package dev.triumphteam.website.api

import io.ktor.resources.Resource

@Resource("/api")
public object Api {

    @Resource("/project")
    public data class Project(
        public val parent: Api = Api,
        public val version: String? = null,
        public val project: String,
    )

    @Resource("/projects")
    public data class Projects(public val parent: Api = Api)

    @Resource("/page")
    public data class Page(
        public val parent: Api = Api,
        public val version: Int,
        public val page: String,
    )
}
