package dev.triumphteam.website.api

import io.ktor.resources.Resource

@Resource("/internal-api")
public object InternalApi {

    @Resource("/setup")
    public data class Setup(public val parent: InternalApi = InternalApi)
}
