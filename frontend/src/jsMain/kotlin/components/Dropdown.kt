package dev.triumphteam.frontend.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.TagMarker
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.router.navigate

@TagMarker
public fun FlowContent.dropdown(id: String, anchor: String, children: FlowContent.() -> Unit) {
    div(
        id = id,
        className = "$anchor open:flex flex-col bg-dark-surface rounded-lg p-2 mt-2 shadow-lg border border-white/10 gap-1",
    ) {
        popover = "auto"
        children()
    }
}

@TagMarker
public fun FlowContent.dropdownItem(text: String, destination: String) {
    navigate(
        to = destination,
        className = "px-3 py-1.5 rounded-md text-sm text-dark-text-primary/80 hover:text-dark-text-primary hover:bg-(--project-color) cursor-pointer transition duration-200 ease-in-out",
    ) {
        text(text)
    }
}
