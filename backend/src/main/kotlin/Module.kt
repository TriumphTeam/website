package dev.triumphteam.backend

import dev.triumphteam.backend.api.apiRoutes
import dev.triumphteam.backend.api.auth.TriumphPrincipal
import dev.triumphteam.backend.meilisearch.Meili
import dev.triumphteam.backend.website.websiteRoutes
import dev.triumphteam.website.JsonSerializer
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.CachingOptions
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing

/** Module of the application. */
public fun Application.module() {

    val propertyValue = System.getenv("WEBSITE_AUTH")
    val bearer = when {
        developmentMode -> "test"
        propertyValue == null -> error("Bearer token not set. Application cannot initiate.")
        else -> propertyValue
    }

    install(Resources)
    install(ContentNegotiation) {
        json(JsonSerializer.json)
    }

    if (developmentMode) {
        install(CallLogging)
    }

    install(CORS) {
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }
    install(Authentication) {
        bearer("bearer") {
            // realm = "Access to the '/api' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == bearer) TriumphPrincipal else null
            }
        }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
    install(ForwardedHeaders)
    install(XForwardedHeaders)

    install(Meili) {
        this.apiKey = bearer
        this.host = System.getenv("MEILI_HOST") ?: "localhost"
    }

    routing {

        staticResources("/static", "static")
        staticFiles("/assets", DATA_FOLDER.resolve("core"))

        install(CachingHeaders) {
            options { _, outgoingContent ->
                when (outgoingContent.contentType?.withoutParameters()) {
                    ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                    ContentType.Image.PNG -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                    else -> null
                }
            }
        }

        apiRoutes()
        websiteRoutes(developmentMode)
    }
}
