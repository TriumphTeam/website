package dev.triumphteam.frontend.components

import dev.triumphteam.horizon.component.functional.FunctionalComponent
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.TagMarker
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.mutableStateOf
import kotlinx.browser.document
import org.w3c.dom.Node
import org.w3c.dom.events.Event

public fun FlowContent.dropdownComponent(
    id: String,
    title: String?,
    decorate: String = "",
    button: FlowContent.(openedState: MutableState<Boolean>) -> Unit,
    children: FlowContent.() -> Unit,
) {
    component {

        val openedState = rememberDropdownState(id)

        render {
            div(id = id, className = "relative inline-flex flex-col items-center") {
                button(openedState)
                dropdown(opened = openedState.get(), decorate = decorate, title = title, children = children)
            }
        }
    }
}

@TagMarker
public fun FlowContent.dropdown(
    opened: Boolean,
    title: String?,
    decorate: String = "",
    children: FlowContent.() -> Unit,
) {

    val stateClass = if (opened) "dropdown-open" else "dropdown-closed"

    div(
        className = "$decorate $stateClass dropdown open:flex items-center z-[100] justify-center flex-col min-w-32 overflow-hidden rounded-xl border border-white/10 bg-dark-surface/95 py-2 mt-2 shadow-xl backdrop-blur-md",
    ) {
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
    dropdownItem { className ->
        navigate(to = destination, className = className) {
            text(text)
        }
    }
}

@TagMarker
public inline fun FlowContent.dropdownItem(
    builder: FlowContent.(className: String) -> Unit,
) {
    builder("w-full p-2 text-sm font-medium text-dark-text-primary/80 hover:text-dark-text-primary hover:bg-(--project-color) cursor-pointer transition")
}

public fun FunctionalComponent.rememberDropdownState(id: String): MutableState<Boolean> {
    val openedState = remember(false)

    val handleOutsideClick: (Event) -> Unit = clickHandler@{ event ->
        val sidebar = document.getElementById(id) ?: return@clickHandler
        val target = event.target as? Node

        if (sidebar.contains(target)) return@clickHandler
        openedState.set(false)
    }

    onCreate {
        document.addEventListener("click", handleOutsideClick)
    }

    onDestroy {
        document.removeEventListener("click", handleOutsideClick)
    }

    return openedState
}
