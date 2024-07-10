package dev.triumphteam.backend.website.pages.docs.components

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id

public data class DropdownOption(
    public val text: String,
    public val link: String,
    public val selected: Boolean = false,
)

public fun FlowContent.dropDown(options: List<DropdownOption>) {
    div {
        classes = setOf("mx-auto", "max-w-sm", "flex", "select-none")

        // Left side of the chooser
        div {
            classes = setOf(
                "inline-flex",
                "flex-shrink-0",
                "items-center",
                "rounded-s-lg",
                "bg-search-bg",
                "px-4",
                "py-2.5",
                "text-center",
                "text-md",
            )

            +"Version"
        }

        val selected = options.find { it.selected } ?: return@div

        // Drop-down menu
        div dropdown@{

            div {

                id = "version-select-button"

                val conditional = listOf(
                    "hover:scale-105",
                    "transition ease-in-out delay-100",
                    "cursor-pointer",
                )

                classes = setOf(
                    "inline-flex",
                    "flex-shrink-0",
                    "items-center",
                    "gap-y-2",
                    "w-24",
                    "rounded-e-lg",
                    "project-color-bg",
                    "p-2.5",
                    "text-lg",
                    "focus:outline-none",
                    "border-0",
                    "align-middle",
                    "justify-center",
                    "font-bold",
                    "text-center",
                ).plus(if (options.size <= 1) emptyList() else conditional)

                +selected.text
            }

            if (options.size <= 1) return@dropdown

            div {
                attributes["hx-boost"] = "true"
                id = "version-select"

                classes = setOf(
                    "invisible opacity-0 ease-in duration-150",
                    "bg-search-bg",
                    "absolute",
                    "w-38",
                    "py-1",
                    "mt-1",
                    "rounded-lg",
                )

                options.sortedByDescending(DropdownOption::text).forEach { option ->
                    a {
                        href = option.link

                        val color = if (option.selected) "project-color" else ""

                        div {
                            classes = setOf(
                                "w-24",
                                "px-4",
                                "py-2",
                                "font-bold",
                                "text-center",
                                "project-color-hover",
                                color,
                            )

                            +option.text
                        }
                    }
                }
            }
        }
    }
}
