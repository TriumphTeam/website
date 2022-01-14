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
            val id = it[Projects.id]
            val type = if (it[Projects.type] == 0u) "plugin" else "library"
            val name = it[Projects.name]
            val icon = it[Projects.icon]
            val version = it[Projects.version]
            val color = it[Projects.color]

            type to Project(id, name, icon, version, color.split(";"))
        }.groupBy({ it.first }, { it.second })
    }

    call.respond(projects)
}

@Serializable
private data class Project(
    val id: String,
    val name: String,
    val icon: String,
    val version: String,
    val color: List<String>,
)