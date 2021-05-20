@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend.feature

import dev.triumphteam.backend.CONFIG
import dev.triumphteam.backend.config.Settings
import dev.triumphteam.backend.events.GithubEvent
import dev.triumphteam.backend.events.GithubPush
import dev.triumphteam.backend.func.receiveNullable
import dev.triumphteam.backend.location.Webhook
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.feature
import io.ktor.application.featureOrNull
import io.ktor.application.install
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.util.AttributeKey
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.pipeline.ContextDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.path.ExperimentalPathApi

/**
 *  Listener feature for handling events
 */
class Listener(val pipeline: Application) {

    /**
     * Handles the github events that are registered
     */
    inline fun <reified T : GithubEvent> on(crossinline action: Github.() -> Unit) {
        pipeline.routing {
            // For now just simple things, might add more later
            if (T::class != GithubPush::class) return@routing

            post<Webhook> {
                // TODO reject non github requests
                val webhookData = call.receiveNullable<PushWebhook>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                // Always reply accepted, since it was handled
                call.respond(HttpStatusCode.Accepted)

                if (CONFIG[Settings.REPO].name != webhookData.repository.fullName) {
                    return@post
                }

                action(application.feature(Github))
            }
        }
    }

    /**
     * Serializable class for getting the repository data
     */
    @Serializable
    data class PushWebhook(val repository: WebhookRepository)

    /**
     * Serializable class for getting the repository name when the event
     * is called to guarantee it's the same repository
     */
    @Serializable
    data class WebhookRepository(@SerialName("full_name") val fullName: String)

    /**
     * Feature companion
     */
    companion object Feature : ApplicationFeature<Application, Listener, Listener> {
        override val key = AttributeKey<Listener>("Listener")

        override fun install(pipeline: Application, configure: Listener.() -> Unit): Listener {
            return Listener(pipeline).apply(configure)
        }
    }

}

/**
 * Listening function to apply the feature just like the `routing` is done in the application
 */
@ContextDsl
fun Application.listening(configuration: Listener.() -> Unit): Listener =
    featureOrNull(Listener)?.apply(configuration) ?: install(Listener, configuration)