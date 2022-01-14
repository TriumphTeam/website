package dev.triumphteam.backend.routing

import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.func.getProject
import dev.triumphteam.backend.location.Api
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import org.jetbrains.exposed.sql.transactions.transaction

@OptIn(KtorExperimentalLocationsAPI::class)
fun Routing.summaryRoute() = get<Api.Project.Summary> { location ->
    val summary = transaction {
        val project = getProject(location.parent.type, location.project) ?: return@transaction null

        project[Projects.summary]
    } ?: run {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }

    call.respondText(summary, ContentType.Application.Json)
}