@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.event.GithubPush
import dev.triumphteam.backend.event.listen
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.func.kotlinx
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.location.Api
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.util.KtorExperimentalAPI
import kotlin.io.path.ExperimentalPathApi

fun Application.module() {

    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) { json(kotlinx) }

    // Custom
    install(Github) { client = makeClient() }
    install(Project)

    routing {
        get<Api.Test> {
            println("Hello")
            call.respondText("Test")
        }

        listen<GithubPush> {
            log { "Detected Github push" }
            checkRepository()
        }

    }
}