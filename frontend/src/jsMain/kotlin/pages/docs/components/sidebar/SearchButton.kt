package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.span

public fun FlowContent.searchButton(version: String) {
    div(
        className = "flex items-center w-full mx-auto bg-dark-surface rounded-lg h-12 cursor-pointer",
    ) {

        div(className = "w-full") {
            span(
                className = "w-full px-4 py-1 rounded-full focus:outline-none pointer-events-none text-white/50 select-none",
            ) {
                text("Search")
            }
        }
        div {
            div(className = "flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg") {
                i(className = "w-5 h-5 fa-solid fa-magnifying-glass")
            }
        }
    }
    /*if (open) {
        searchArea(version = version, onClose = { open = false })
    }*/
}
