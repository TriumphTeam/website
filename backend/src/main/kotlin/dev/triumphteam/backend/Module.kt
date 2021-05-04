@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class
)

package dev.triumphteam.backend

import com.ryanharter.ktor.moshi.moshi
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
import io.ktor.util.KtorExperimentalAPI

fun Application.module() {
    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) {
        moshi()
    }

    val client = makeClient()

    routing {
        get<Api.Test> {
            println("Hello")
            call.respondText("Test")
        }
    }
}