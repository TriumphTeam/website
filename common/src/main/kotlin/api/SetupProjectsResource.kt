package dev.triumphteam.website.api

import io.ktor.resources.Resource

@Resource("/api")
public object Api {

    @Resource("/setup")
    public data class Setup(public val parent: Api = Api)
}
