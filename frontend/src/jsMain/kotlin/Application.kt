package dev.triumphteam.frontend

import dev.triumphteam.frontend.api.api
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PROJECT_PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.VERSION_PROJECT_VARIABLE
import dev.triumphteam.frontend.pages.docs.docs
import dev.triumphteam.frontend.pages.docs.provideDocsRoute
import dev.triumphteam.frontend.pages.home.home
import dev.triumphteam.frontend.state.fold
import dev.triumphteam.frontend.state.rememberApiCallState
import dev.triumphteam.horizon.app
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.div
import dev.triumphteam.website.serializable.PROJECT_ROUTE
import dev.triumphteam.website.serializable.ProjectVersion
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

public fun main() {
    app {

        index { home() }

        route(
            path = "docs/:$VERSION_PROJECT_VARIABLE?/:$PROJECT_PAGE_VARIABLE?/:$PAGE_VARIABLE?",
            routeProvider = { _, variables -> provideDocsRoute(variables) },
        ) { route ->

            component {
                // The docs page directly depends on the version and project.
                // If either of them changes, we need to do a full re-render.
                val project by remember(route.projectState)
                val projectDataResult by rememberApiCallState {
                    api.get(urlString = PROJECT_ROUTE) {
                        parameter("project", project.project)
                        parameter("version", project.version)
                    }.body<ProjectVersion>()
                }

                render {
                    projectDataResult.fold(
                        onSuccess = {
                            docs(route.pageState, data)
                        },
                        onWaiting = {
                            div(className = "fixed inset-0 skeleton-shimmer")
                        },
                    )
                }
            }
        }
    }
}
