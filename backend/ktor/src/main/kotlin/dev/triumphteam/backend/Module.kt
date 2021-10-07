@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.events.GithubPush
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.feature.Placeholders
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.feature.listening
import dev.triumphteam.backend.func.JSON
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.routing.pageContentRoute
import dev.triumphteam.backend.routing.pageRoute
import dev.triumphteam.backend.routing.summaryRoute
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.serialization.json
import kotlin.io.path.ExperimentalPathApi

/**
 * Module of the application
 */
fun Application.module() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }
    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) { json(JSON) }

    // Custom
    install(Github) { client = makeClient() }
    install(Project)

    val placeholders = install(Placeholders)

    listening {
        on<GithubPush> {
            log { "Detected Github push." }
            checkRepository()
        }
    }

    routing {
        summaryRoute()
        pageRoute(placeholders)
        pageContentRoute()
    }

}
