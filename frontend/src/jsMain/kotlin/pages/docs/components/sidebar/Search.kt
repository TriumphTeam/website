package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.input
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.state.MutableState
import org.w3c.dom.events.Event

private const val SEARCH_CLASS = "flex items-center bg-dark-surface border border-white/10 rounded-lg h-12"

public fun FlowContent.searchButton() {
    component {

        val openedState = remember(true)

        render {
            div(className = "cursor-pointer") {
                onClick = { openedState.set(true) }

                div(className = "$SEARCH_CLASS w-full") {
                    div(className = "w-full") {
                        span(className = "w-full px-4 py-1 text-white rounded-full focus:outline-none pointer-events-none text-white/50 select-none") {
                            text("Search")
                        }
                    }
                    div {
                        div(className = "flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg") {
                            i(className = "w-5 h-5 bx bx-search")
                        }
                    }
                }
            }

            searchArea(openedState)
        }
    }
}

private fun FlowContent.searchArea(openedState: MutableState<Boolean>) {
    val classes = if (openedState.get()) "opacity-100 pointer-events-auto" else "opacity-0 pointer-events-none"

    div(className = "$classes fixed inset-0 bg-black/50 backdrop-blur-xs z-100 flex items-center justify-center") {
        onClick = { openedState.set(false) }
        div(className = "$SEARCH_CLASS w-lg") {
            onClick = Event::stopPropagation
            div(className = "w-full") {
                input(className = "w-full px-4 py-1 text-white rounded-full focus:outline-none text-white/50 select-none") {
                    placeholder = "Search"
                }
            }
            div {
                div(className = "flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg") {
                    i(className = "w-5 h-5 bx bx-search")
                }
            }
        }
    }
}
