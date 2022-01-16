package dev.triumphteam.backend.routing

import dev.triumphteam.backend.database.Pages
import dev.triumphteam.backend.feature.Placeholders
import dev.triumphteam.backend.func.getPage
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
fun Routing.pageRoute(placeholders: Placeholders) = get<Api.Project.Page> { location ->
    val page = transaction {
        getPage(location.parent.type, location.project, location.page)?.get(Pages.content)
    } ?: run {
        call.respond(HttpStatusCode.NotFound)
        return@get
    }


    call.respondText(placeholders.replace(location.project, page), ContentType.Text.Html)
}