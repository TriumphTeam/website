package dev.triumphteam.backend.website.pages.docs.components

import kotlinx.html.FlowContent
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id

public fun FlowContent.toast() {
    div {

        id = "toast"

        classes = setOf(
            "fixed bottom-0 right-0",
            "bg-red-700",
            "w-72 h-24",
            "mx-12 my-4",
            "flex justify-center items-stretch",
            "bg-search-bg",
            "border-2 border-neutral-700",
            "opacity-0 ease-in duration-150",
            "pointer-events-none",
        )

        div {
            classes = setOf("self-auto p-4 flex-1 flex items-center justify-center")

            +"Code copied to clipboard!"
        }
    }
}
