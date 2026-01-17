package dev.triumphteam.backend.api.routing

import ProjectVersion
import VersionData
import dev.triumphteam.backend.database.PageEntity
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.database.ProjectEntity
import dev.triumphteam.backend.database.Projects
import dev.triumphteam.backend.database.VersionEntity
import dev.triumphteam.backend.database.Versions
import dev.triumphteam.website.api.Api
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("api-route")

public fun Routing.apiRoutes() {
    get<Api.Project> { api ->
        val project = api.project
        val version = api.version

        val projectEntity = transaction {
            ProjectEntity.findById(project)
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        val versionEntity = transaction {
            when {
                version == null -> VersionEntity.find { (Versions.project eq project) and (Versions.default eq true) }
                else -> VersionEntity.find { (Versions.reference eq version) and (Versions.project eq project) }
            }.firstOrNull()
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(
            ProjectVersion(
                project = project,
                name = projectEntity.name,
                version = versionEntity.id.value,
                document = versionEntity.versionDocument,
            )
        )
    }

    get<Api.Projects> {
        val projects = transaction {
            Projects.innerJoin(Versions, additionalConstraint = { Projects.id eq Versions.project })
                .selectAll()
                .groupBy { Projects.id }
                .map { (id, rows) ->
                    val firstRow = rows.first()
                    ProjectData(
                        id = firstRow[id].value,
                        name = firstRow[Projects.name],
                        color = firstRow[Projects.color],
                        versions = rows.map { row ->
                            VersionData(
                                reference = row[Versions.reference],
                                current = row[Versions.default],
                            )
                        }.sortedBy(VersionData::reference),
                    )
                }
        }

        call.respond(projects)
    }

    get<Api.Page> { api ->
        val pageEntity = transaction {
            PageEntity.find { (Pages.reference eq api.page) and (Pages.version eq api.version) }.firstOrNull()
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(pageEntity.content)
    }

    get<Api.SearchData> { searchData ->
        val searchData = transaction {
            VersionEntity.findById(searchData.version)?.searchSections
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        return@get call.respond(searchData)
    }
}

@Serializable
public data class ProjectData(
    public val id: String,
    public val name: String,
    public val color: String,
    public val versions: List<VersionData>,
)
