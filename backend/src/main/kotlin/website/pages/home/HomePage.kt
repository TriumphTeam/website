package dev.triumphteam.backend.website.pages.home

import dev.triumphteam.backend.api.database.DocVersionEntity
import dev.triumphteam.backend.api.database.DocVersions
import dev.triumphteam.backend.api.database.ProjectEntity
import dev.triumphteam.backend.website.pages.backgroundBlob
import dev.triumphteam.backend.website.pages.createIconPath
import dev.triumphteam.backend.website.pages.home.resource.Home
import dev.triumphteam.backend.website.pages.setupHead
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.resources.get
import io.ktor.server.routing.Routing
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.style
import kotlinx.html.title
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

public fun Routing.homeRoutes(developmentMode: Boolean) {

    get<Home> {
        call.respondHtml {
            val projects = transaction {
                ProjectEntity.all().map { project ->
                    val version = DocVersionEntity.find {
                        (DocVersions.project eq project.id) and (DocVersions.recommended eq true)
                    }.first()

                    ProjectDisplay(
                        id = project.id.value,
                        name = project.name,
                        icon = createIconPath(project.id.value),
                        color = project.color,
                        version = version.id.value,
                    )
                }
            }

            fullPage(developmentMode, projects)
        }
    }
}

private fun HTML.fullPage(developmentMode: Boolean, projects: List<ProjectDisplay>) {

    setupHead(developmentMode) {

        link {
            href = "/static/css/home_style.css"
            rel = "stylesheet"
        }

        title("TriumphTeam")
    }

    body {

        classes = setOf("bg-black", "text-white", "w-screen", "h-full", "sans", "overflow-x-hidden")

        // Add a "haze" effect to the screen
        div {
            classes = setOf("fixed", "w-screen", "h-screen", "bg-white/5", "pointer-events-none")
        }

        // Main grid
        div {
            classes = setOf("grid", "grid-cols-6", "gap-4", "justify-items-center")

            backgroundBlob(
                properties = listOf(
                    "-translate-y-[400px]",
                    "-translate-x-[300px]",
                    "w-[50rem]",
                    "h-[50rem]",
                ),
            )

            backgroundBlob(
                properties = listOf(
                    "translate-y-[100px]",
                    "translate-x-[400px]",
                    "w-[60rem]",
                    "h-[60rem]",
                ),
            )

            backgroundBlob(
                properties = listOf(
                    "-translate-y-[200px]",
                    "-translate-x-[700px]",
                    "w-[90rem]",
                    "h-[90rem]",
                ),
            )

            logoArea()
            centerArea()
            projectsArea(projects)
        }
    }
}

private fun FlowContent.logoArea() {
    // Empty area on top
    div { classes = setOf("col-span-6", "h-32") }

    // Logo area
    div {
        classes = setOf("col-start-3", "col-span-2", "h-44")

        img(src = "/static/images/logo.png")
    }
}

private fun FlowContent.centerArea() {
    div {
        classes =
            setOf("col-start-2", "col-span-4", "text-center", "grid", "grid-cols-1", "justify-items-center")

        div {
            classes =
                setOf("max-w-32", "col-span-1", "bg-primary", "rounded-full", "px-4", "py-1")
            +"TRIUMPH"
        }

        div {
            classes = setOf("col-span-1", "sans-bold", "py-2", "text-6xl")
            +"TRIUMPH TEAM"
        }

        div {
            classes = setOf("col-span-1", "py-2", "text-xl", "w-5/6")
            +"Home to our documentations."
        }

        div {
            classes = setOf("col-span-1", "py-10", "grid", "grid-cols-2", "gap-10")

            socialButton(BorderDirection.LEFT, "fa-github", "https://github.com/TriumphTeam")
            socialButton(BorderDirection.RIGHT, "fa-discord", "/discord")
        }
    }
}

private fun FlowContent.socialButton(direction: BorderDirection, icon: String, link: String) {
    a {

        classes = setOf("z-10")

        href = link

        div {
            classes = setOf(
                "col-span-1",
                "bg-primary",
                "px-12",
                "py-5",
                "border-left",
                direction.clazz,
                "cursor-pointer",
                "transition ease-in-out delay-100",
                "hover:bg-primary-light",
                "hover:scale-110",
            )

            i {
                classes = setOf("fa-brands", icon, "text-4xl", "text-black")
            }
        }
    }
}

private fun FlowContent.projectsArea(projects: List<ProjectDisplay>) {
    div {
        classes = setOf(
            "col-start-1",
            "col-span-6",
            "xl:col-start-2",
            "xl:col-span-4",
            "bg-card-bg",
            "w-full",
            "md:w-3/5",
            "grid",
            "grid-cols-1",
            "xl:grid-cols-2",
            "gap-8",
            "justify-items-center",
            "projects-shape",
            "py-20",
            "px-16",
            "z-10",
        )

        projects.chunked(2).forEach { projects ->
            projects.forEach(::projectCard)
        }
    }
}

private fun FlowContent.projectCard(project: ProjectDisplay) {
    a {
        href = "/docs/${project.id}"

        div {
            style = "border-color: ${project.color}7F"
            classes = setOf(
                "col-span-1",
                "flex flex-col justify-items-center gap-2",
                "w-64",
                "bg-card-bg-secondary",
                "py-6",
                "rounded-3xl",
                "border",
                "transition ease-in-out delay-100",
                "hover:scale-110",
            )

            // Image
            div {
                classes = setOf("flex justify-center", "py-4")

                img(src = project.icon, classes = "w-28")
            }

            div {
                classes = setOf("flex justify-center")
                h1 {
                    classes = setOf("sans-bold", "text-2xl")
                    +project.name
                }
            }

            div {
                classes = setOf("flex justify-center")

                div {
                    style = "background-color: ${project.color};"
                    classes =
                        setOf("py-1", "px-4", "rounded-md", "w-auto", "text-base", "text-lg")
                    +project.version
                }
            }
        }
    }
}

private enum class BorderDirection(val clazz: String) {
    LEFT("border-left"), RIGHT("border-right");
}

private data class ProjectDisplay(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val version: String,
)
