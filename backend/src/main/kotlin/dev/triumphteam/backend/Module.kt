@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class
)

package dev.triumphteam.backend

import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.feature.Github
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
import me.mattstudios.config.SettingsManager
import java.nio.file.Paths

fun Application.module() {

    val config = SettingsManager
        .from(Paths.get("data", "config.yml"))
        .configurationData(Settings::class.java)
        .create()

    val client = makeClient()

    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) {
        json()
    }
    install(Github) {
        config(config)
        client(client)
    }

    routing {
        get<Api.Test> {
            println("Hello")
            call.respondText("Test")
        }

    }
}