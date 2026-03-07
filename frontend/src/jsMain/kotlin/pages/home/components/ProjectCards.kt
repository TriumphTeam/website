package dev.triumphteam.frontend.pages.home.components

import dev.triumphteam.frontend.api.API_BASE_URL
import dev.triumphteam.frontend.api.api
import dev.triumphteam.frontend.state.fold
import dev.triumphteam.frontend.state.rememberApiCallState
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.website.serializable.PROJECTS_ROUTE
import dev.triumphteam.website.serializable.ProjectData
import io.ktor.client.call.body
import io.ktor.client.request.get

public fun FlowContent.projects() {
    div(className = "flex flex-wrap items-center justify-center gap-4 mt-12") {
        component {
            val example by rememberApiCallState {
                api.get(urlString = PROJECTS_ROUTE).body<List<ProjectData>>()
            }

            render {
                example.fold(
                    onSuccess = {
                        data.forEach { project ->
                            projectBadge(Project(project.id, project.name))
                        }
                    },
                    onWaiting = {
                        projectBadge(null)
                        projectBadge(null)
                    },
                )
            }
        }
    }
}

private fun FlowContent.projectBadge(project: Project?) {
    val classes =
        "group flex items-center gap-3 px-4 py-2.5 bg-dark-surface/60 backdrop-blur-sm rounded-md border border-dark-surface hover:border-primary/50 hover:scale-105 transition-all duration-200 hover:shadow-lg hover:shadow-primary/10"

    if (project == null) {
        div(className = "$classes skeleton-shimmer h-8 w-32")
        return
    }

    navigate(
        to = "/docs/${project.id}/introduction",
        className = "group flex items-center gap-3 px-4 py-2.5 bg-dark-surface/60 backdrop-blur-sm rounded-md border border-dark-surface hover:border-primary/50 hover:scale-105 transition-all duration-200 hover:shadow-lg hover:shadow-primary/10",
    ) {
        img(
            className = "w-6 h-6 object-contain",
            src = "$API_BASE_URL/assets/${project.id}/icon.png",
            alt = "${project.id} logo",
        )
        span(className = "text-dark-text-primary/80 group-hover:text-dark-text-primary text-sm font-medium uppercase transition-colors duration-200") {
            text(project.name)
        }
    }
}

private data class Project(val id: String, val name: String)
