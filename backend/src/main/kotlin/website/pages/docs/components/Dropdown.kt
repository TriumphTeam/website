package dev.triumphteam.backend.website.pages.docs.components

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id

public fun FlowContent.dropDown(versions: Set<String>, current: String) {
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
                    "bg-primary",
                    "p-2.5",
                    "text-lg",
                    "focus:outline-none",
                    "border-0",
                    "align-middle",
                    "justify-center",
                    "font-bold",
                    "text-center",
                ).plus(if (versions.size <= 1) emptyList() else conditional)

                +current
            }

            if (versions.size <= 1) return@dropdown

            div {
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

                // TODO
                repeat(4) {
                    a {
                        href = "#"

                        div {
                            classes = setOf("px-4", "py-2")

                            +"Option $it"
                        }
                    }
                }
            }
        }
    }
}
