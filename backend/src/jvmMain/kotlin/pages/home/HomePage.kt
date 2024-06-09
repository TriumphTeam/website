package dev.triumphteam.backend.pages.home

import dev.triumphteam.backend.pages.home.resource.Home
import dev.triumphteam.backend.pages.setupHead
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
import kotlinx.html.h2
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.title

public fun Routing.homeRoutes() {

    get<Home> {

        call.respondHtml {
            fullPage()
        }
    }
}

private fun HTML.fullPage() {

    setupHead {

        link {
            href = "/static/css/home_style.css"
            rel = "stylesheet"
        }

        title("TriumphTeam")
    }

    body {

        classes = setOf("bg-black", "text-white", "w-screen", "h-full", "sans")

        // Add a "haze" effect to the screen
        div {
            classes = setOf("fixed", "w-screen", "h-screen", "bg-white/5", "pointer-events-none")
        }

        div {
            classes = setOf("blob")
        }

        // Main grid
        div {
            classes = setOf("grid", "grid-cols-6", "gap-4", "justify-items-center")

            logoArea()
            centerArea()
            projectsArea()
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
            classes = setOf("col-span-1", "py-2", "text-2xl", "w-3/6")
            +"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris ipsum massa, venenatis ac pretium at."
        }

        div {
            classes = setOf("col-span-1", "py-10", "grid", "grid-cols-2", "gap-10")

            socialButton(BorderDirection.LEFT, "fa-github")
            socialButton(BorderDirection.RIGHT, "fa-discord")
        }
    }
}

private fun FlowContent.socialButton(direction: BorderDirection, icon: String) {
    a {

        classes = setOf("z-10")

        href = "#"

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

private fun FlowContent.projectsArea() {
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
            "px-16"
        )

        projectCard(CardSide.LEFT)
        projectCard(CardSide.RIGHT)
    }
}

private fun FlowContent.projectCard(side: CardSide) {
    a {
        href = "/docs"

        classes = setOf(
            "col-span-1",
            "grid",
            "grid-cols-3",
            "gap-0",
            "justify-items-center",
            "content-center",
            "w-full",
            "bg-card-bg-secondary",
            "py-6",
            "rounded-3xl",
            "border-primary/50",
            "border",
            "transition ease-in-out delay-100",
            "hover:scale-110",
            "z-10",
            side.clazz,
        )

        // Image
        div {

            classes = setOf("col-span-1", "grid", "content-center")

            img(src = "/static/images/test.png", classes = "w-20")
        }

        // Descriptions
        div {
            classes = setOf(
                "col-span-2",
                "w-full",
                "grid",
                "grid-cols-1",
                "gap-1",
                "justify-items-center",
                "content-center",
                "text-center",
                "pr-4"
            )

            h1 {
                classes = setOf("col-span-1", "sans-bold", "text-2xl", "w-full")
                +"Project Name"
            }

            div {
                classes = setOf("col-span-1", "bg-primary", "py-1", "px-3", "rounded-md")
                +"1.0.0"
            }

            h2 {
                classes = setOf("col-span-1", "sans-bold", "text-sm", "w-full")

                +"Small description of the project"
            }
        }
    }
}

private enum class CardSide(val clazz: String) {
    LEFT("justify-self-end"), RIGHT("justify-self-start");
}

private enum class BorderDirection(val clazz: String) {
    LEFT("border-left"), RIGHT("border-right");
}
