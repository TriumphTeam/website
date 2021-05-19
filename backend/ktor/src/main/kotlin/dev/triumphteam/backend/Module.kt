@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.func.kotlinx
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.location.Api
import dev.triumphteam.backend.location.Webhook
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

        post<Webhook> {
            val webhookData = call.receive<PushWebhook>()
            // Always reply accepted, since it was handled
            call.respond(HttpStatusCode.Accepted)

            if (CONFIG[Settings.REPO].name != webhookData.repository.fullName) {
                return@post
            }

            log { "Detected Github push" }
        }

    }
}

@Serializable
data class PushWebhook(val repository: WebhookRepository)

@Serializable
data class WebhookRepository(@SerialName("full_name") val fullName: String)