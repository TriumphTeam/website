package dev.triumphteam.frontend

import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.PROJECT_PAGE_VARIABLE
import dev.triumphteam.frontend.pages.docs.DocsRoute.Companion.VERSION_PROJECT_VARIABLE
import dev.triumphteam.frontend.pages.docs.docs
import dev.triumphteam.frontend.pages.docs.provideDocsRoute
import dev.triumphteam.frontend.pages.home.home
import dev.triumphteam.frontend.state.fold
import dev.triumphteam.horizon.app
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.div

public fun main() {
    app {

        index { home() }

        route(
            path = "docs/:$VERSION_PROJECT_VARIABLE?/:$PROJECT_PAGE_VARIABLE?/:$PAGE_VARIABLE?",
            routeProvider = { scope, _, variables -> provideDocsRoute(scope, variables) },
        ) { route ->

            component {
                // The docs page directly depends on the version and project.
                // If either of them changes, we need to do a full re-render.
                val projectResult by remember(route.projectState)

                render {
                    projectResult.fold(
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
