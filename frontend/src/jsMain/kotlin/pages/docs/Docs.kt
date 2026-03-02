package dev.triumphteam.frontend.pages.docs

import dev.triumphteam.frontend.components.DEFAULT_BACKGROUND
import dev.triumphteam.frontend.components.DOTTED_BACKGROUND
import dev.triumphteam.frontend.internal.now
import dev.triumphteam.frontend.pages.docs.components.sidebar.navigationArea
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectButtons
import dev.triumphteam.frontend.pages.docs.components.sidebar.projectHeader
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.state.State
import dev.triumphteam.website.serializable.ProjectVersion

private const val RESPONSIVE_BAR_POSITION = "fixed hidden xl:flex xl:static"
private const val SIDE_BAR_CLASSES = "flex-col gap-4 px-4 justify-center noise"

public fun FlowContent.docs(pageState: State<String>, projectData: ProjectVersion) {
    div(className = "w-screen h-screen bg-darker-background p-3") {
        style = "--project-color: ${projectData.document.color}"

        div(className = "flex flex-row gap-3 h-full") {
            sideBar(pageState, projectData)
            content()
            onThisPage()
        }
    }
}

private fun FlowContent.sideBar(pageState: State<String>, projectData: ProjectVersion) {
    val document = projectData.document

    div(className = "$RESPONSIVE_BAR_POSITION $SIDE_BAR_CLASSES $DEFAULT_BACKGROUND md:w-72 xl:min-w-60 xl:w-60 2xl:min-w-72 2xl:w-72 rounded-lg") {
        projectHeader(projectData)
        projectButtons(document.discord, document.github, document.javadocs)
        navigationArea(pageState, document)
        smallFooter()
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

private fun FlowContent.smallFooter() {
    div(className = "flex-none text-[0.5em] text-center py-2") {
        text("Copyright © 2020-${now.getFullYear()}, TriumphTeam. All Rights Reserved.")
    }
}
