package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.frontend.components.dropdown
import dev.triumphteam.frontend.components.dropdownItem
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.website.serializable.VersionSetting

public fun FlowContent.controlBar(settings: List<VersionSetting>, settingStates: Map<String, MutableState<String>>) {
    div(className = "fixed left-1/2 -translate-x-1/2 bottom-6 border border-white/10 bg-dark-background/80 p-2 noise backdrop-blur-md rounded-lg text-dark-text-primary shadow-[0px_0px_14px_0px_rgba(0,_0,_0,_0.6)]") {
        div(className = "flex justify-center items-center gap-2") {
            div(className = "flex-grow flex justify-center items-center gap-2") {
                settings.forEach { setting ->
                    val state = settingStates[setting.id] ?: return@forEach

                    val hasValues = setting.values.size > 1

                    val hoverClass = if (hasValues) "hover:bg-(--project-color)" else ""
                    val cursorClass = if (hasValues) "cursor-pointer" else ""

                    val dropdownId = "${setting.id}-dropdown"
                    val anchor = "setting-dropdown-${setting.id}"

                    div(className = "relative") {

                        component {
                            var stateValue by remember(state)

                            render {
                                barButton(
                                    decorate = "min-w-32 bg-dark-surface $hoverClass $cursorClass text-sm",
                                    tooltip = setting.name,
                                ) {
                                    style = "anchor-name: --$anchor;"
                                    popoverTarget = dropdownId
                                    span { text(stateValue) }
                                    if (hasValues) i(className = "bx bx-chevron-down")
                                }
                            }
                        }

                        /*if (hasValues) {
                            dropdown(id = dropdownId, anchor = "setting-dropdown") {
                                dropdownItem("test", "test")
                            }
                        }*/
                        dropdown(
                            id = dropdownId,
                            decorate = ""
                        ) {
                            dropdownItem("test", "test")
                        }
                    }
                }
            }

            div(className = "w-px h-6 bg-white/10")

            div(className = "") {
                barButton(
                    decorate = "bg-(--project-color) hover:bg-(--project-color)/90 text-md w-11 cursor-pointer",
                ) {
                    i(className = "bx bx-search")
                }
            }
        }
    }
}
