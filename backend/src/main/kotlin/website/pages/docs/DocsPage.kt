package dev.triumphteam.backend.website.pages.docs

import dev.triumphteam.backend.website.pages.docs.components.dropDown
import dev.triumphteam.backend.website.pages.docs.components.search
import dev.triumphteam.backend.website.pages.setupHead
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.title
import java.time.LocalDate

public fun Routing.docsRoutes() {

    get("/docs/{param...}") {

        val param = call.parameters.extractDocsPath() ?: return@get call.respond(HttpStatusCode.NotFound)

        println(param)

        call.respondHtml {
            renderFullPage()
        }
    }
}

private fun HTML.renderFullPage() {
    setupHead {

        link {
            href = "/static/css/docs_style.css"
            rel = "stylesheet"
        }

        script {
            src = "https://unpkg.com/htmx.org@1.9.12"
        }

        title { +"TrimphTeam | Docs" }
    }

    body {

        classes = setOf("bg-docs-bg", "overflow-hidden", "text-white", "flex gap-4")

        sideBar()
        content()

        script {
            src = "/static/scripts/script.js"
        }
    }
}

private fun FlowContent.content() {
    // Content area
    div {

        classes = setOf("flex-initial", "w-screen h-screen", "p-12")

        // Actual content
        div {
            classes = setOf("h-full", "w-full", "flex", "justify-center")
            div {

                classes = setOf("w-3/4")
                +"Content area"
            }
        }

        // Footer
        div {

            classes = setOf("text-xs", "text-center")
            +"Copyright © ${LocalDate.now().year}, TriumphTeam. All Rights Reserved."
        }
    }

    // Right side, bar area
    div {

        classes = setOf("flex-none", "w-80 h-screen", "px-6", "py-16")

        div {
            classes = setOf("py-4", "px-2")
            a {
                href = "#"
                classes = setOf("w-full", "text-white/75", "text-sm")
                +"Edit this page on GitHub"
            }
        }

        h1 {
            classes = setOf("text-2xl", "font-bold", "py-2")
            +"On this page"
        }

        repeat(4) {
            div {
                classes = setOf("py-1")

                a {
                    href = "#"
                    classes = setOf("text-white/75 text-lg")
                    +"Option $it"
                }
            }
        }
    }
}

private fun FlowContent.sideBar() {
    div {

        classes = setOf(
            "flex-none",
            "w-72",
            "h-screen",
            // "bg-indigo-500",
            "grid",
            "grid-cols-1",
            "gap-6",
            "px-4",
            "justify-items-center",
        )

        // Logo area
        div {
            classes = setOf("col-span-1", "grid", "grid-cols-1", "gap-4", "w-full", "justify-items-center")

            img(src = "/static/images/logo.png", classes = "col-span-1 w-36")

            div {
                classes = setOf("col-span-1", "text-center", "font-bold", "text-xl")

                h1 {
                    +"Project Name"
                }
            }

            div {
                classes = setOf("col-span-1")

                dropDown()
            }
        }

        search()

        div {
            classes = setOf(
                "col-span-1",
                "w-full",
                "h-screen",
                "px-4",
                "overflow-y-auto",
                "overflow-x-hidden",
            )

            div {

                classes = setOf(
                    "grid",
                    "grid-cols-1",
                    "gap-10",
                )

                barHeader("Welcome")
                barHeader("Welcome")
                barHeader("Welcome")
                barHeader("Welcome")
                barHeader("Welcome")
            }
        }
    }
}

private fun FlowContent.barHeader(text: String) {
    div {
        classes = setOf("h-full")

        h1 {
            classes = setOf("text-white", "text-xl", "font-bold")

            +text
        }

        page("Introduction")
    }
}

private fun FlowContent.page(text: String) {
    div {

        classes = setOf("py-2")

        a {
            href = "#"

            classes = setOf("text-white/70", "text-lg", "hover:text-primary", "transition ease-in-out delay-100")

            +text
        }
    }
}
