package dev.triumphteam.backend.api.routing

import dev.triumphteam.backend.DATA_FOLDER
import dev.triumphteam.backend.api.setupRepository
import dev.triumphteam.website.api.InternalApi
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.resources.Resource
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveMultipart
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

private val logger: Logger = LoggerFactory.getLogger("setup-route")

@Resource("/test/{name}")
public data class Test(public val name: String)

public fun Routing.setupRoutes() {

    get<Test> { test ->
        call.respond(HttpStatusCode.OK, test)
    }

    authenticate("bearer") {
        post<InternalApi.Setup> {
            runCatching {
                call.receiveMultipart()
            }.fold(
                onSuccess = { multipartData ->
                    // Handle parsing
                    multipartData.forEachPart { part ->
                        when (part) {
                            is PartData.FileItem -> {
                                val fileBytes = part.streamProvider().readBytes()
                                val downloadsFolder = DATA_FOLDER.resolve("downloads").apply(File::mkdirs)
                                val zip = downloadsFolder.resolve("projects.zip").also {
                                    it.writeBytes(fileBytes)
                                }
                                setupRepository(zip)
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
