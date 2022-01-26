package dev.triumphteam.backend.routing

import dev.triumphteam.backend.database.Contents
import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.func.getPage
import dev.triumphteam.backend.location.ProjectLocation
import dev.triumphteam.markdown.content.ContentData
import dev.triumphteam.markdown.content.ContentEntry
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@OptIn(KtorExperimentalLocationsAPI::class)
fun Routing.pageContentRoute() =  get<ProjectLocation.ContentLocation> { location ->
    val contentData = transaction {
        val page = getPage(location.parent.type, location.project, location.page) ?: return@transaction null

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