@file:OptIn(
    KtorExperimentalLocationsAPI::class,
    KtorExperimentalAPI::class,
    ExperimentalPathApi::class,
)

package dev.triumphteam.backend

import dev.triumphteam.backend.database.Entries
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.event.GithubPush
import dev.triumphteam.backend.event.listen
import dev.triumphteam.backend.feature.Github
import dev.triumphteam.backend.feature.Project
import dev.triumphteam.backend.func.kotlinx
import dev.triumphteam.backend.func.log
import dev.triumphteam.backend.func.makeClient
import dev.triumphteam.backend.func.mapEntry
import dev.triumphteam.backend.location.Api
import dev.triumphteam.markdown.summary.Summary
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.ForwardedHeaderSupport
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.util.KtorExperimentalAPI
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.ExperimentalPathApi
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Application.module() {

    install(Locations)
    install(ForwardedHeaderSupport)
    install(ContentNegotiation) { json(kotlinx) }

    // Custom
    install(Github) { client = makeClient() }
    install(Project)

    routing {
        // TODO move to listener instead of routing
        listen<GithubPush> {
            log { "Detected Github push." }
            checkRepository()
        }

        get<Api.Test> {
            println("Hello")
            call.respondText("Test")
        }

        get<Api.Summary> { location ->
            val summary = transaction {
                val project = Projects.select {
                    Projects.name eq location.project
                }.firstOrNull() ?: return@transaction null

                val entries = Entries.select {
                    Entries.project eq project[Projects.id]
                }.andWhere {
                    Entries.parent.isNull()
                }.orderBy(Entries.position).mapNotNull { mapEntry(it) }

                Summary(entries)
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(summary)
        }

    }
}