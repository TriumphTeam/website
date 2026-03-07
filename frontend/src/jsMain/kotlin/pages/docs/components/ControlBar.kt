package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.FlowTag
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.span
import dev.triumphteam.website.serializable.VersionSetting

public fun FlowContent.controlBar(settings: List<VersionSetting>) {
    div(className = "fixed left-1/2 -translate-x-1/2 bottom-6 border border-white/10 bg-dark-background/80 p-2 backdrop-blur-md rounded-lg text-dark-text-primary") {
        div(className = "flex justify-center items-center gap-2") {
            div(className = "flex-grow flex justify-center items-center gap-2") {
                settings.forEach { setting ->
                    barButton(
                        decorate = "min-w-32 bg-dark-surface hover:bg-(--project-color) text-sm",
                        tooltip = setting.id,
                    ) {
                        span { text(setting.values.first()) }
                        i(className = "bx bx-chevron-down")
                    }
                }
            }

            div(className = "w-px h-6 bg-white/10")

            div(className = "") {
                barButton(
                    decorate = "bg-(--project-color) hover:bg-(--project-color)/90 text-md w-11",
                ) {
                    i(className = "bx bx-search")
                }
            }
        }
    }
}
