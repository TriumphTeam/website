package dev.triumphteam.backend.website.pages.docs.components

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.span
import kotlinx.html.unsafe

public fun FlowContent.search(
    enabled: Boolean,
    project: String = "",
    version: String = "",
) {
    div {
        classes = setOf("flex", "items-center", "w-full", "mx-auto", "bg-search-bg", "rounded-lg", "h-12")

        div {
            classes = setOf("w-full")

            val textClasses =
                setOf("w-full", "px-4", "py-1", "text-white", "rounded-full", "bg-search-bg", "focus:outline-none")

            if (enabled) {
                input {
                    attributes["hx-get"] = "/search?p=${project}&v=${version}"
                    attributes["hx-trigger"] = "keyup changed delay:500ms"
                    attributes["hx-target"] = "#results"

                    type = InputType.search
                    classes = textClasses
                    placeholder = "Search"
                    name = "q"
                    autoComplete = false
                }
            } else {
                span {
                    classes = textClasses.plus("pointer-events-none text-white/50 select-none")
                    +"Search"
                }
            }
        }

        div {
            div {
                classes = setOf(
                    "flex",
                    "items-center",
                    "justify-center",
                    "w-12",
                    "h-12",
                    "text-white/35",
                    "rounded-r-lg",
                )

                i {
                    classes = setOf("w-5", "h-5", "fa-solid", "fa-magnifying-glass")
                }
            }
        }
    }
}

public fun FlowContent.searchArea(project: String, version: String) {
    div {
        id = "search-area"

        classes = setOf(
            "invisible opacity-0 ease-in duration-150",
            "fixed",
            "w-screen h-screen",
            "docs-search",
            "flex justify-center items-center",
        )

        div {

            id = "search-area-container"

            classes = setOf(
                "w-5/6 xl:w-4/6 2xl:w-3/5 h-3/5",
                "bg-docs-bg",
                "rounded-lg",
                "py-4 px-12",
                "flex flex-col gap-6",
            )

            search(true, project, version)

            div {
                id = "results"

                classes = setOf(
                    "flex flex-col gap-6",
                    "text-white/80",
                )

                noResults()
            }
        }
    }
}

public fun FlowContent.noResults(query: String = "") {
    div {

        classes = setOf("w-full text-center", "pt-12", "text-xl", "text-white/50")

        +if (query.isBlank()) {
            "No results"
        } else {
            "No results for \"$query\""
        }
    }
}

public fun FlowContent.searchResult(title: String, description: String, link: String) {
    a {

        href = link

        div {

            classes = setOf(
                "w-full",
                "flex flex-col",
                "border border-zinc-700 rounded-lg",
                "project-color-hover",
                "project-color-border",
            )

            div {

                classes = setOf("w-full", "p-2", "font-bold text-lg")

                +title
            }

            div {

                classes = setOf("w-full", "p-2", "text-sm")

                unsafe {
                    raw(description)
                }
            }
        }
    }
}
