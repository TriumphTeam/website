package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.horizon.state.mutableStateOf
import org.w3c.dom.HTMLDivElement

public fun FlowContent.projectHeader() {
    div(className = "grid grid-cols-1 w-full justify-items-center gap-4 pt-6 select-none") {
        div(className = "flex items-center") {
            navigate(to = "/") {
                img(
                    className = "self-center h-16",
                    src = "http://localhost:8001/assets/triumph-gui/icon.png",
                    alt = "Logo",
                )
            }
        }
        div(className = "flex items-center gap-2") {
            div(className = "col-span-1 text-center font-bold text-lg uppercase") {
                h1 {
                    text("project name")
                }
            }
            // versionComponent(key = "version-component", project = project, versions = versions)
        }
    }
}

/*public fun FlowContent.versionComponent(project: String, versions: List<VersionData>) {
    val open = remember { mutableStateOf(false) }
    val ref = useRef<HTMLDivElement>()

    val isSingular = versions.size <= 1
    val cursor = if (isSingular) "default" else "pointer"
    val current = versions.find { it.current }

    if (current == null) return

    div(
        className = "relative flex items-center justify-center rounded-sm bg-(--project-color) px-2 text-center text-md cursor-$cursor",
        ref = ref,
        onClick = { open.value = !open.value },
    ) {
        text(current.reference)
        if (open.value && versions.size > 1) {
            dropdown(key = "version-dropdown", small = true) {
                versions.forEach { version ->
                    dropdownItem(
                        key = "version-dropdown-${version.reference}",
                        text = version.reference,
                        destination = "docs/${version.reference}/$project/introduction",
                    )
                }
            }
        }
    }
}*/
