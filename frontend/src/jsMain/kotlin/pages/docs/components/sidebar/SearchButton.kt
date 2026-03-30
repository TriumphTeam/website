package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.frontend.internal.showModal
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.FlowTag
import dev.triumphteam.horizon.html.dialog
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.span

public fun FlowContent.searchButton(versison: String, s: Int) {
    // val modalTag = searchModal()

    div(
        className = "flex items-center w-full mx-auto bg-dark-surface rounded-lg h-12 cursor-pointer",
    ) {
        // onClick = { modalTag.element.showModal() }

        div(className = "w-full") {
            span(
                className = "w-full px-4 py-1 rounded-full focus:outline-none pointer-events-none select-none",
            ) {
                text("Search")
            }
        }
        div {
            div(className = "flex items-center justify-center w-12 h-12 dark-text-secondary rounded-r-lg") {
                i(className = "w-5 h-5 bx bx-search")
            }
        }
    }
    /*if (open) {
        searchArea(version = version, onClose = { open = false })
    }*/
}

public fun FlowContent.searchModal(): FlowTag {
    return dialog(
        id = "search-modal",
        className = "bg-transparent w-4xl z-50 fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 flex justify-center items-center",
        closedBy = "any",
    ) {
        div(className = "w-4/5 flex justify-center items-center") {
            // searchBar()
            div {
                text("penis")
            }
        }
    }
}
