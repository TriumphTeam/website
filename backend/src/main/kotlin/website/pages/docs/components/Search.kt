package dev.triumphteam.backend.website.pages.docs.components

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label

public fun FlowContent.search(enabled: Boolean) {
    div {
        classes = setOf("flex", "items-center", "w-full", "mx-auto", "bg-search-bg", "rounded-lg", "h-12")

        div {
            classes = setOf("w-full")

            val textClasses =
                setOf("w-full", "px-4", "py-1", "text-white", "rounded-full", "bg-search-bg", "focus:outline-none")

            if (enabled) {
                input {
                    type = InputType.search
                    classes = textClasses
                    placeholder = "Search"
                }
            } else {
                label {
                    classes = textClasses.plus("pointer-events-none text-white/50")
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

public fun FlowContent.searchArea() {
    div {
        id = "search-area"

        classes = setOf(
            "invisible opacity-0 ease-in duration-150",
            "fixed",
            "w-screen h-screen",
            "docs-search",
            "p-72",
        )

        div {

            id = "search-area-container"

            classes = setOf(
                "w-2/5 min-h-72",
                "bg-docs-bg",
                "mx-auto",
                "my-0",
                "rounded-lg",
                "py-4 px-12",
                "flex flex-col gap-6",
            )

            search(true)

            noResults()
        }
    }
}

private fun FlowContent.noResults() {
    div {

        classes = setOf("w-full text-center", "pt-12")

        +"..."
    }
}

private fun FlowContent.searchResult(title: String, subtitle: String) {
    div {

        classes = setOf("w-full", "flex flex-col", "border border-zinc-700 rounded-lg")

        div {

            classes = setOf("w-full", "p-2", "font-bold text-lg")

            +title
        }

        div {

            classes = setOf("w-full", "p-2", "text-sm")

            +subtitle
        }
    }
}
