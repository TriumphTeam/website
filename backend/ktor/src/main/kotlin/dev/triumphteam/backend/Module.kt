@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.database.Contents
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.database.Summaries
import dev.triumphteam.backend.events.GithubPush
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.feature.listening
import dev.triumphteam.backend.func.JSON
import dev.triumphteam.backend.func.getPage
import dev.triumphteam.backend.func.getProject
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.location.Api
import dev.triumphteam.markdown.content.ContentData
import dev.triumphteam.markdown.content.ContentEntry
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.ExperimentalPathApi

/**
 * Module of the application
 */
fun Application.module() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }
    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) { json(JSON) }

    // Custom
    install(Github) { client = makeClient() }
    install(Project)

    listening {
        on<GithubPush> {
            log { "Detected Github push." }
            checkRepository()
        }
    }

    routing {

        get<Api.Summary> { location ->
            val summary = transaction {
                val project = getProject(location.project) ?: return@transaction null

                Summaries.select {
                    Summaries.project eq project[Projects.id]
                }.firstOrNull()
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respondText(summary[Summaries.content], ContentType.Application.Json)
        }

        get<Api.Page> { location ->
            val page = transaction {
                getPage(location.project, location.page)?.get(Pages.content)
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respondText(page, ContentType.Text.Html)
        }

        get<Api.Content> { location ->
            val contentData = transaction {
                val page = getPage(location.project, location.page) ?: return@transaction null

                val entries = Contents.select {
                    Contents.page eq page[Pages.id]
                }.orderBy(Contents.position)
                    .map {
                        ContentEntry(it[Contents.literal], it[Contents.href], it[Contents.indent])
                    }

                ContentData(page[Pages.github], entries)
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(contentData)
        }

    }

}
