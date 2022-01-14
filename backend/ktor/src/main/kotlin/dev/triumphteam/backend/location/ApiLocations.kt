@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class
)

package dev.triumphteam.backend.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.util.KtorExperimentalAPI

@Location("/api")
open class Api {

    @Location("/projects")
    open class Projects

    @Location("/project/{type}")
    data class Project(val type: String) {

        @Location("/summary/{project}")
        data class Summary(val parent: Project, val project: String)

        @Location("/page/{project}/{page}")
        data class Page(val parent: Project, val project: String, val page: String)

        @Location("/content/{project}/{page}")
        data class Content(val parent: Project, val project: String, val page: String)

    }

}