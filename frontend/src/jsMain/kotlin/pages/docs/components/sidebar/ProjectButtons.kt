package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.frontend.pages.docs.components.barLink
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i

public fun FlowContent.projectButtons(discord: String?, github: String?, javadocs: String?) {
    div(className = "flex justify-center items-center gap-2 px-4") {
        projectButton(tooltip = "Discord", icon = "bxl bx-discord-alt", link = discord)
        projectButton(tooltip = "Github", icon = "bxl bx-github", link = github)
        projectButton(tooltip = "Javadocs", icon = "bx bx-book", link = javadocs)
    }
}

private fun FlowContent.projectButton(tooltip: String, icon: String, link: String?) {
    val classes = when {
        link != null -> "bg-dark-surface hover:bg-(--project-color) text-dark-text-primary"
        else -> "bg-dark-surface/60 text-dark-text-primary/10"
    }

    barLink(
        decorate = "w-1/3 p-2 $classes",
        tooltip = if (link != null) tooltip else null,
        link = link,
        small = true,
    ) {
        i(className = icon)
    }
}
