package dev.triumphteam.frontend.pages.home.components

import dev.triumphteam.frontend.state.fold
import dev.triumphteam.frontend.state.rememberApiCallState
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.a
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.span

public fun FlowContent.projects() {
    div(className = "flex flex-wrap items-center justify-center gap-4 mt-12") {
        component {
            val example by rememberApiCallState(test = { "example" })

            render {
                example.fold(
                    onSuccess = {
                        projectBadge(Project(data, data, data, listOf(data)))
                        projectBadge(Project(data, data, data, listOf(data)))
                        projectBadge(Project(data, data, data, listOf(data)))
                    },
                    onFailure = {},
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
    a(
        className = "group flex items-center gap-3 px-4 py-2.5 bg-dark-surface/60 backdrop-blur-sm rounded-md border border-dark-accent hover:border-primary/50 hover:scale-105 transition-all duration-200 hover:shadow-lg hover:shadow-primary/10",
    ) {
        if (project == null) {
            div(className = "w-6 h-6 border-2 border-primary/30 border-t-primary rounded-full animate-spin")
            span(className = "text-white/40 text-sm font-medium") {
                text("Loading...")
            }
        } else {
            img(
                className = "w-6 h-6 object-contain",
                src = project.logo,
                // alt = "${project.name} logo",
            )
            span(className = "text-white/80 group-hover:text-white text-sm font-medium transition-colors duration-200") {
                text(project.name)
            }
        }
    }
}

private data class Project(
    val logo: String,
    val name: String,
    val description: String,
    val versions: List<String>,
)
