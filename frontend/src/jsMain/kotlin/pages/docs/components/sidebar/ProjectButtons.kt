package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.a
import dev.triumphteam.horizon.html.attributes.Target
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
    val isEnabled = link != null
    val baseClassName = "w-1/3 flex justify-center items-center bg-dark-surface rounded-md p-2"

    if (!isEnabled) {
        div(className = "$baseClassName text-white/10") {
            i(className = icon)
        }
        return
    }

    a(
        href = link,
        target = Target.BLANK,
        rel = "noopener noreferrer",
        className = "$baseClassName transition duration-300 ease-in-out hover:bg-(--project-color)",
        attributes = mutableMapOf("data-tooltip" to tooltip),
    ) {
        className = "$baseClassName transition duration-300 ease-in-out hover:bg-(--project-color)"
        i(className = icon)
    }
}
