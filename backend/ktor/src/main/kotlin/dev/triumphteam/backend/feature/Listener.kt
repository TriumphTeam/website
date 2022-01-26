@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend.feature

import dev.triumphteam.backend.events.GithubEvent
import dev.triumphteam.backend.func.receiveNullable
import dev.triumphteam.backend.location.WebhookLocation
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
class Listener(pipeline: Application) {

    val listeners = mutableListOf<GitHubResult.() -> Unit>()

    init {
        pipeline.routing {
            post<WebhookLocation> {
                // TODO reject non github requests
                val webhookData = call.receiveNullable<PushWebhook>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                // Always reply accepted, since it was handled
                call.respond(HttpStatusCode.Accepted)

                listeners.forEach {
                    it(GitHubResult(application.feature(Github), webhookData.repository.fullName))
                }
            }
        }
    }

    /**
     * Handles the GitHub events that are registered
     */
    inline fun <reified T : GithubEvent> on(noinline action: GitHubResult.() -> Unit) {
        listeners.add(action)
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

data class GitHubResult(val github: Github, val project: String)

/**
 * Listening function to apply the feature just like the `routing` is done in the application
 */
@ContextDsl
fun Application.listening(configuration: Listener.() -> Unit): Listener =
    featureOrNull(Listener)?.apply(configuration) ?: install(Listener, configuration)