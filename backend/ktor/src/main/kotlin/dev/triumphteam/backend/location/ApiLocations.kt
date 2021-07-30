@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class
)

package dev.triumphteam.backend.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.util.KtorExperimentalAPI

@Location("/api")
object Api {

    @Location("/test")
    class Test

    @Location("/summary/{project}")
    data class Summary(val project: String)

    @Location("/page/{project}/{page}")
    data class Page(val project: String, val page: String)

    @Location("/content/{project}/{page}")
    data class Content(val project: String, val page: String)

}