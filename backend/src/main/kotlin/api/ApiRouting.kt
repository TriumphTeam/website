package dev.triumphteam.backend.api

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.website.api.Api
import dev.triumphteam.website.project.Repository
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
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
                call.receiveMultipart()
            }.fold(
                onSuccess = { multipartData ->
                    // Handle parsing
                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                val fileBytes = part.streamProvider().readBytes()
                                DATA_FOLDER.resolve("downloads/projects.zip").writeBytes(fileBytes)
                            }

                            else -> {}
                        }
                        part.dispose()
                    }

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
