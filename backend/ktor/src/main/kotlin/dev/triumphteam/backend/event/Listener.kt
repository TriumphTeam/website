@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend.event

import dev.triumphteam.backend.CONFIG
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.location.Webhook
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.feature
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.path.ExperimentalPathApi

inline fun <reified T : GithubEvent> Route.listen(crossinline action: Github.() -> Unit) {
    // For now just simple things, might add more later
    if (T::class != GithubPush::class) return

    post<Webhook> {
        val webhookData = call.receive<PushWebhook>()
        // Always reply accepted, since it was handled
        call.respond(HttpStatusCode.Accepted)

        if (CONFIG[Settings.REPO].name != webhookData.repository.fullName) {
            return@post
        }

        action(application.feature(Github))
    }
}

@Serializable
data class PushWebhook(val repository: WebhookRepository)

@Serializable
data class WebhookRepository(@SerialName("full_name") val fullName: String)