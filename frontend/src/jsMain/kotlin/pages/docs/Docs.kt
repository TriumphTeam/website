package dev.triumphteam.frontend.pages.docs

import dev.triumphteam.frontend.components.DEFAULT_BACKGROUND
import dev.triumphteam.frontend.components.DOTTED_BACKGROUND
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectHeader
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.state.State
import dev.triumphteam.website.serializable.ProjectVersion

private const val RESPONSIVE_BAR_POSITION = "fixed xl:static"

public fun FlowContent.docs(pageState: State<String>, projectData: ProjectVersion) {
    div(className = "w-screen h-screen bg-darker-background p-3") {
        style = "--project-color: todo"

        div(className = "flex flex-row gap-3 h-full") {
            sideBar()
            content()
            onThisPage()
        }
    }
}

private fun FlowContent.sideBar() {
    div(className = "$RESPONSIVE_BAR_POSITION $DEFAULT_BACKGROUND md:w-72 xl:min-w-60 xl:w-60 2xl:min-w-72 2xl:w-72 rounded-lg") {
        projectHeader()
    }
}

private fun FlowContent.content() {
    div(className = "$DEFAULT_BACKGROUND w-full rounded-lg") {
        div(className = "$DOTTED_BACKGROUND w-full h-full") {

        }
    }
}

private fun FlowContent.onThisPage() {
    div(className = "$RESPONSIVE_BAR_POSITION $DEFAULT_BACKGROUND w-128 rounded-lg") {

    }
}
