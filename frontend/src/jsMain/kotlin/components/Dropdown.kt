package dev.triumphteam.frontend.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.TagMarker
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.navigate

@TagMarker
public fun FlowContent.dropdown(
    id: String,
    decorate: String = "",
    title: String? = "version",
    children: FlowContent.() -> Unit,
) {
    div(
        id = id,
        className = "$decorate dropdown open:flex items-center justify-center hidden flex-col min-w-32 overflow-hidden rounded-xl border border-white/10 bg-dark-surface/95 py-2 mt-2 shadow-xl backdrop-blur-md",
    ) {
        popover = "auto"

        if (title != null) {
            div(className = "py-1 px-2 mb-1 border-b border-white/10 text-center") {
                span(className = "text-xs font-semibold uppercase tracking-wide text-dark-text-primary/50") {
                    text(title)
                }
            }
        }

        div(className = "flex flex-col items-center justify-center text-center gap-1 w-full") {
            children()
        }
    }
}

@TagMarker
public fun FlowContent.dropdownItem(text: String, destination: String) {
    navigate(
        to = destination,
        className = "w-full p-2 text-sm font-medium text-dark-text-primary/80 hover:text-dark-text-primary hover:bg-(--project-color) cursor-pointer transition",
    ) {
        text(text)
    }
}
