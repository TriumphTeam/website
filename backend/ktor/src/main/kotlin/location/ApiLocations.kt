@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class
)

package dev.triumphteam.backend.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.util.KtorExperimentalAPI

@Location("/projects")
open class ProjectsLocation

@Location("/project/{type}")
data class ProjectLocation(val type: String) {

    @Location("/summary/{project}")
    data class SummaryLocation(val parent: ProjectLocation, val project: String)

    @Location("/page/{project}/{page}")
    data class PageLocation(val parent: ProjectLocation, val project: String, val page: String)

    @Location("/content/{project}/{page}")
    data class ContentLocation(val parent: ProjectLocation, val project: String, val page: String)

}