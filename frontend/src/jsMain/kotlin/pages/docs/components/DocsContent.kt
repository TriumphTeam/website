package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.frontend.api.API_BASE_URL
import dev.triumphteam.frontend.api.api
import dev.triumphteam.frontend.components.DOTTED_BACKGROUND
import dev.triumphteam.frontend.state.fold
import dev.triumphteam.frontend.state.rememberApiCallState
import dev.triumphteam.frontend.state.rememberSectionsState
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.a
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.h2
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.horizon.state.State
import dev.triumphteam.website.serializable.PAGE_ROUTE
import dev.triumphteam.website.serializable.PageContent
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.ProjectVersion
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

public fun FlowContent.pageContent(pageState: State<String>, projectData: ProjectVersion) {
    component {

        val page by remember(pageState)

        render {
            content(page, projectData)
        }
    }
}

private fun FlowContent.content(page: String, projectData: ProjectVersion) {
    component {

        val result by rememberApiCallState {
            api.get(PAGE_ROUTE) {
                parameter("project", projectData.project)
                parameter("version", projectData.version)
                parameter("page", page)
            }.body<PageDocument>()
        }

        render {
            result.fold(
                onSuccess = {
                    page(pageDocument = data)
                    tableOfContents(sections = data.sections)
                },
                onWaiting = {
                    // TODO: Show skeleton.
                },
            )
        }
    }
}

private fun FlowContent.page(pageDocument: PageDocument) {
    val banner = pageDocument.banner
    val previous = pageDocument.previous
    val next = pageDocument.next

    div(className = "$DOTTED_BACKGROUND relative flex-grow flex flex-col min-h-0 min-w-0 px-6 items-center") {
        // Fade backgrounds to make the dots a little nicer.
        div(className = "absolute inset-y-0 left-0 w-12 bg-gradient-to-r from-darker-background to-transparent pointer-events-none z-10")
        div(className = "absolute inset-y-0 right-0 w-12 bg-gradient-to-l from-darker-background to-transparent pointer-events-none z-10")

        div(id = "doc-content", className = "flex-1 [&>*]:px-2 pt-12 w-full md:w-8/10") {
            h1(className = "text-4xl font-medium text-dark-text-primary text-center pointer-events-none") {
                text(pageDocument.name)
            }
            h2(className = "text-lg text-center") {
                text(pageDocument.description)
            }
            separator()
            if (banner != null) {
                div(className = "flex justify-center items-center") {
                    img(className = "w-4/5", src = "$API_BASE_URL/assets/${banner}", alt = "page-banner")
                }
            }

            docsComponents(
                document = pageDocument,
                /*buildToolState = buildToolState,
                languageState = languageState,
                platformState = platformState,*/
            )
        }
        div(className = "px-2 mt-12 pb-8 flex items-center justify-between gap-2 text-sm w-full md:w-8/10") {
            div {
                if (previous != null) {
                    navigate(
                        to = "../${previous.id}",
                        className = "flex items-center gap-2 hover:text-(--project-color) transition ease-in-out",
                    ) {
                        i(className = "bx bx-chevron-left")
                        span {
                            text(previous.name)
                        }
                    }
                }
            }
            div {
                if (next != null) {
                    navigate(
                        to = "../${next.id}",
                        className = "flex items-center gap-2 hover:text-(--project-color) transition ease-in-out",
                    ) {
                        span {
                            text(next.name)
                        }
                        i(className = "bx bx-chevron-right")
                    }
                }
            }
        }
    }
}

private fun FlowContent.tableOfContents(sections: List<PageContent>) {
    div(className = "hidden sticky top-0 self-start max-h-screen min-w-72 w-72 lg:flex flex-col pt-12 px-2") {
        div(className = "flex gap-1 text-lg font-bold items-center mb-2 text-dark-text-primary") {
            i(className = "bx bx-menu-select")
            h2(className = "text-center") {
                text("On this page")
            }
        }

        div(className = "border-l-1 border-dark-surface") {
            component {

                val intersectedSections by rememberSectionsState()

                render {
                    sections.forEach { section ->
                        val selected = section.id in intersectedSections

                        val border = when {
                            selected -> "border-l-3 border-(--project-color)"
                            else -> "border-l-3 border-transparent hover:border-l-3 hover:border-(--project-color)/20"
                        }

                        div(className = "w-full $border -ml-[2px] px-2 py-1") {
                            section(section, selected = selected)
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.section(section: PageContent, selected: Boolean) {
    val color = if (selected) "text-(--project-color)" else ""

    val level = when (section.level) {
        2 -> "ml-6 text-sm"
        3 -> "ml-12"
        else -> ""
    }

    div(className = "$level $color px-2 hover:text-(--project-color)") {
        a(href = "#${section.id}") {
            text(section.name)
        }
    }
}
