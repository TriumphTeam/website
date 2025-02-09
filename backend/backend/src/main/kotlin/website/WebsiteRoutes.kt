package dev.triumphteam.backend.website

import dev.triumphteam.backend.meilisearch.Meili
import dev.triumphteam.backend.website.pages.docs.docsRoutes
import dev.triumphteam.backend.website.pages.home.homeRoutes
import dev.triumphteam.backend.website.pages.respondNotFound
import io.ktor.server.application.call
import io.ktor.server.application.plugin
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

public fun Routing.websiteRoutes(developmentMode: Boolean) {
    val meili = plugin(Meili)

    homeRoutes(developmentMode)
    docsRoutes(meili, developmentMode)

    get("404") {
        call.respondHtml {
            respondNotFound(call.application.developmentMode)
        }
    }
}
