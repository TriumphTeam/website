package dev.triumphteam.frontend.pages.docs

import dev.triumphteam.frontend.pages.docs.components.controlBar
import dev.triumphteam.frontend.pages.docs.components.pageContent
import dev.triumphteam.frontend.pages.docs.components.sidebar
import dev.triumphteam.frontend.state.localStorageState
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.state.State
import dev.triumphteam.website.serializable.ProjectVersion
import dev.triumphteam.website.serializable.SettingValue

public fun FlowContent.docs(pageState: State<String>, projectData: ProjectVersion) {
    val settingStates = projectData.document.settings.associate { setting ->
        setting.id to localStorageState(
            key = setting.id,
            default = setting.values.first(),
            transform = { stored ->
                setting.values.find { stored == it.id }
            },
            reverseTransform = SettingValue::id,
        )
    }

    div(className = "bg-darker-background w-screen min-h-screen") {
        style = "--project-color: ${projectData.document.color}"

        div(className = "flex flex-row gap-3 w-full min-h-full") {

            sidebar(pageState, projectData)
            pageContent(pageState, projectData, settingStates)
        }
    }
}
