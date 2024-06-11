package dev.triumphteam.backend.website

import dev.triumphteam.backend.website.pages.docs.docsRoutes
import dev.triumphteam.backend.website.pages.home.homeRoutes
import io.ktor.server.routing.Routing

public fun Routing.websiteRoutes() {
    homeRoutes()
    docsRoutes()
}
