package dev.triumphteam.backend.website

import dev.triumphteam.backend.meilisearch.Meili
import dev.triumphteam.backend.website.pages.docs.docsRoutes
import dev.triumphteam.backend.website.pages.home.homeRoutes
import io.ktor.server.application.plugin
import io.ktor.server.routing.Routing

public fun Routing.websiteRoutes(developmentMode: Boolean) {
    val meili = plugin(Meili)

    homeRoutes(developmentMode)
    docsRoutes(meili, developmentMode)
}
