package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.frontend.components.DEFAULT_BACKGROUND
import dev.triumphteam.frontend.internal.now
import dev.triumphteam.frontend.pages.docs.components.sidebar.navigationArea
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectButtons
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectHeader
import dev.triumphteam.frontend.pages.docs.components.sidebar.searchButton
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.State
import dev.triumphteam.horizon.state.mutableStateOf
import dev.triumphteam.website.serializable.ProjectVersion
import kotlinx.browser.document
import org.w3c.dom.Node
import org.w3c.dom.events.Event

private const val RESPONSIVE_BAR_POSITION = "flex -translate-x-[120%] xl:translate-none z-[50] fixed xl:sticky top-0"
private const val SIDEBAR_SIZES = "w-screen lg:w-72 xl:min-w-60 xl:w-60 2xl:min-w-72 2xl:w-72"
private const val SIDEBAR_CLASSES = "flex-col gap-4 px-4 justify-center noise"

private const val SIDEBAR_ID = "sidebar"

public fun FlowContent.sidebar(pageState: State<String>, searchOpenedState: MutableState<Boolean>, projectData: ProjectVersion) {
    component {
        val openState = mutableStateOf(false)
        var opened by remember(openState)

        val handleOutsideClick: (Event) -> Unit = clickHandler@{ event ->
            val sidebar = document.getElementById(SIDEBAR_ID) ?: return@clickHandler
            val target = event.target as? Node

            if (sidebar.contains(target)) return@clickHandler
            opened = false
        }

        onCreate {
            document.addEventListener("click", handleOutsideClick)
        }

        onDestroy {
            document.removeEventListener("click", handleOutsideClick)
        }

        render {
            val openClasses = if (opened) "translate-x-0" else ""
            val buttonIcon = if (opened) "bx-x rotate-90" else "bx-menu rotate-none"

            // The open button.
            div(className = "xl:hidden fixed z-[80] top-0 text-2xl pl-6 py-8") {
                button(className = "cursor-pointer") {
                    onClick = { event ->
                        event.stopPropagation()
                        opened = !opened
                    }
                    i(className = "transition-transform bx $buttonIcon")
                }
            }

            // The bottom part is the desktop version.
            div(
                id = SIDEBAR_ID,
                className = "sidebar $openClasses $RESPONSIVE_BAR_POSITION $SIDEBAR_SIZES $SIDEBAR_CLASSES $DEFAULT_BACKGROUND h-screen rounded-r-lg",
            ) {
                sideBarContent(pageState, searchOpenedState, projectData, openState)
            }
        }
    }
}

private fun FlowContent.sideBarContent(
    pageState: State<String>,
    searchOpenedState: MutableState<Boolean>,
    projectData: ProjectVersion,
    openState: MutableState<Boolean>,
) {
    val document = projectData.document

    projectHeader(projectData)
    projectButtons(document.discord, document.github, document.javadocs)
    searchButton(searchOpenedState)
    navigationArea(pageState, document, openState)
    smallFooter()
}

private fun FlowContent.smallFooter() {
    div(className = "flex-none text-[0.5em] text-center py-2") {
        text("Copyright © 2020-${now.getFullYear()}, TriumphTeam. All Rights Reserved.")
    }
}
