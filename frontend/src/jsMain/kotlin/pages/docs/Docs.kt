package dev.triumphteam.frontend.pages.docs

import dev.triumphteam.frontend.components.DEFAULT_BACKGROUND
import dev.triumphteam.frontend.components.DOTTED_BACKGROUND
import dev.triumphteam.frontend.internal.now
import dev.triumphteam.frontend.pages.docs.components.pageContent
import dev.triumphteam.frontend.pages.docs.components.sidebar.navigationArea
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectButtons
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectHeader
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.state.State
import dev.triumphteam.website.serializable.ProjectVersion
import kotlinx.serialization.json.JsonNull.content

private const val RESPONSIVE_BAR_POSITION = "hidden xl:flex xl:sticky top-0"
private const val SIDE_BAR_SIZES = "w-screen lg:w-72 xl:min-w-60 xl:w-60 2xl:min-w-72 2xl:w-72"
private const val SIDE_BAR_CLASSES = "flex-col gap-4 px-4 justify-center noise"

public fun FlowContent.docs(pageState: State<String>, projectData: ProjectVersion) {
    div(className = "bg-darker-background w-screen min-h-screen") {
        div(className = "flex flex-row gap-3 w-full min-h-full") {
            style = "--project-color: ${projectData.document.color}"

            sideBar(pageState, projectData)
            pageContent(pageState, projectData)
        }
    }
}

private fun FlowContent.sideBar(pageState: State<String>, projectData: ProjectVersion) {
    // The top part is the mall screen version.

    // The open button.
    div(className = "xl:hidden fixed z-[80] top-0 text-2xl pl-6 py-8") {
        button(className = "cursor-pointer") {
            popoverTarget = "sidebar-popover"
            i(className = "bx bx-menu")
        }
    }
    // Then the openable sidebar.
    div(
        id = "sidebar-popover",
        className = "small-screen-sidebar fixed h-dvh open:xl:hidden bg-transparent text-default-text fixed z-[100] p-4 $SIDE_BAR_SIZES",
    ) {
        popover = "auto"
        div(className = "absolute top-0 right-0 px-8 py-8 text-2xl") {
            button(className = "cursor-pointer") {
                popoverTarget = "sidebar-popover"
                popoverTargetAction = "hide"
                i(className = "bx bx-x")
            }
        }
        div(className = "flex $SIDE_BAR_CLASSES $DEFAULT_BACKGROUND h-full shadow-[4px_4px_12px_rgba(0,0,0,0.25)] rounded-lg") {
            sideBarContent(pageState, projectData, small = true)
        }
    }

    // The bottom part is the desktop version.
    div(className = "$RESPONSIVE_BAR_POSITION $SIDE_BAR_SIZES $SIDE_BAR_CLASSES $DEFAULT_BACKGROUND h-screen rounded-r-lg") {
        sideBarContent(pageState, projectData, small = false)
    }
}

private fun FlowContent.smallFooter() {
    div(className = "flex-none text-[0.5em] text-center py-2") {
        text("Copyright © 2020-${now.getFullYear()}, TriumphTeam. All Rights Reserved.")
    }
}

private fun FlowContent.sideBarContent(pageState: State<String>, projectData: ProjectVersion, small: Boolean) {
    val document = projectData.document

    projectHeader(projectData, small = small)
    projectButtons(document.discord, document.github, document.javadocs)
    navigationArea(pageState, document)
    smallFooter()
}
