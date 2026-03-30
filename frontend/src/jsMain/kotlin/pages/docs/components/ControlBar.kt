package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.frontend.components.dropdown
import dev.triumphteam.frontend.components.dropdownItem
import dev.triumphteam.frontend.components.rememberDropdownState
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.TagMarker
import dev.triumphteam.horizon.html.button
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.website.serializable.SettingValue
import dev.triumphteam.website.serializable.VersionSetting
import org.w3c.dom.events.Event

private const val BAR_CLASSES = "fixed z-90 left-1/2 -translate-x-1/2 bottom-6 border border-white/10 bg-dark-background " +
        "p-2 noise rounded-lg text-dark-text-primary " +
        "shadow-[0px_0px_14px_0px_rgba(0,_0,_0,_0.6)]"

public fun FlowContent.controlBar(
    settings: List<VersionSetting>,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    div(className = BAR_CLASSES) {
        div(className = "flex justify-center items-center gap-2 text-sm xl:text-md") {
            div(className = "flex-grow flex justify-center items-center gap-2") {
                settings.forEach { setting ->
                    val settingState = settingStates[setting.id] ?: return@forEach

                    val hasValues = setting.values.size > 1

                    val hoverClass = if (hasValues) "hover:bg-(--project-color)" else ""
                    val cursorClass = if (hasValues) "cursor-pointer" else ""

                    val dropdownId = "${setting.id}-dropdown"

                    if (!hasValues) return@forEach

                    component {

                        var opened by rememberDropdownState(dropdownId)
                        var settingValue by remember(settingState)

                        render {
                            div(id = dropdownId, className = "relative inline-flex flex-col items-center") {
                                controlButton(
                                    hoverClass = hoverClass,
                                    cursorClass = cursorClass,
                                    text = settingValue.name,
                                    showArrow = hasValues,
                                    onClick = { opened = !opened },
                                )

                                dropdown(opened = opened, decorate = "bottom-full mb-5", title = setting.name) {
                                    setting.values.forEach { value ->
                                        val selected = settingValue == value
                                        val selectedClass = if (selected) "bg-(--project-color)" else ""

                                        dropdownItem { className ->
                                            button(className = "$className $selectedClass") {
                                                onClick = { settingValue = value }
                                                text(value.name)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            div(className = "w-px h-6 bg-white/10")

            div(className = "") {
                barButton(
                    decorate = "bg-(--project-color) hover:bg-(--project-color)/90 w-11 cursor-pointer",
                ) {
                    i(className = "bx bx-search")
                }
            }
        }
    }
}

@TagMarker
private fun FlowContent.controlButton(
    hoverClass: String,
    cursorClass: String,
    text: String,
    showArrow: Boolean,
    onClick: (Event) -> Unit = {},
) {
    barButton(
        decorate = "min-w-16 xl:min-w-32 bg-dark-surface $hoverClass $cursorClass text-sm",
    ) {
        this.onClick = onClick
        span(className = "hidden lg:inline") {
            text(text)
        }

        i(className = "inline xl:!hidden bx bx-check")

        if (showArrow) i(className = "bx bx-chevron-down")
    }
}
