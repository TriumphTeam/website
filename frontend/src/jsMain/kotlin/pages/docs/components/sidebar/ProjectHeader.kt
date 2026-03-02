package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.frontend.api.API_BASE_URL
import dev.triumphteam.frontend.components.dropdown
import dev.triumphteam.frontend.components.dropdownItem
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.website.serializable.ProjectVersion
import dev.triumphteam.website.serializable.VersionData

private const val VERSION_DROPDOWN_ID = "version-dropdown"

public fun FlowContent.projectHeader(projectData: ProjectVersion) {
    div(className = "grid grid-cols-1 w-full justify-items-center gap-4 pt-6 select-none") {
        div(className = "flex items-center") {
            navigate(to = "/") {
                img(
                    className = "self-center h-16",
                    src = "$API_BASE_URL/assets/${projectData.project}/icon.png",
                    alt = "${projectData.project} logo",
                )
            }
        }
        div(className = "flex items-center gap-2") {
            div(className = "col-span-1 text-center font-bold text-lg uppercase") {
                h1 {
                    text(projectData.name)
                }
            }
            versionComponent(project = projectData.project, versions = projectData.document.versions)
        }
    }
}

public fun FlowContent.versionComponent(project: String, versions: List<VersionData>) {
    val isSingular = versions.size <= 1
    val cursor = if (isSingular) "default" else "pointer"
    val current = versions.find { it.current }

    if (current == null) return

    button(
        className = "version-dropdown-anchor relative flex items-center justify-center rounded-sm bg-(--project-color) px-2 text-center text-md cursor-$cursor",
    ) {
        popoverTarget = VERSION_DROPDOWN_ID
        text(current.reference)
    }

    dropdown(id = VERSION_DROPDOWN_ID, anchor = "version-dropdown") {
        versions.forEach { version ->
            dropdownItem(
                text = version.reference,
                destination = "/docs/${version.reference}/$project/introduction",
            )
        }
    }
}
