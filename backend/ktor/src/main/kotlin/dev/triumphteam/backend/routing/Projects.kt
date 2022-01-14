package dev.triumphteam.backend.routing

import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.location.Api
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

@OptIn(KtorExperimentalLocationsAPI::class)
fun Routing.projectsRoute() = get<Api.Projects> {
    val projects = transaction {
        Projects.selectAll().asSequence().map {
            val type = if (it[Projects.type] == 0u) "plugin" else "library"
            val name = it[Projects.name]
            val version = it[Projects.version]

            type to Project(name, version)
        }.groupBy({it.first}, {it.second})
    }

    call.respond(projects)
}

@Serializable
private data class Project(val name: String, val version: String)