package dev.triumphteam.backend.api

import dev.triumphteam.website.api.Api
import dev.triumphteam.website.project.Repository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("api-route")

public fun Routing.apiRoutes() {

    authenticate("bearer") {
        post<Api.Setup> {
            runCatching {
                call.receive<Repository>()
            }.fold(
                onSuccess = { (editPath, projects) ->
                    // Handle parsing
                    setupRepository(editPath, projects)
                    call.respond(HttpStatusCode.Accepted)
                },
                onFailure = {
                    logger.warn("Attempted to setup repository with wrong request body.")
                    call.respond(HttpStatusCode.BadRequest)
                }
            )
        }
    }
}
