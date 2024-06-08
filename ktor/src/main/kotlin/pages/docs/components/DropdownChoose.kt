package dev.triumphteam.backend.pages.docs.components

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i

public fun FlowContent.dropDownChoose() {
    div {
        classes = setOf("mx-auto", "max-w-sm", "flex", "select-none")

        // Left side of the chooser
        div {
            classes = setOf(
                "inline-flex",
                "flex-shrink-0",
                "items-center",
                "rounded-s-lg",
                "bg-search-bg", // TODO better background color
                "px-4",
                "py-2.5",
                "text-center",
                "text-md",
            )

            +"Version"
        }

        // Drop-down menu
        // FUCK THIS DROP DOWN SHIT
        div {
            div {

                classes = setOf(
                    "inline-flex",
                    "flex-shrink-0",
                    "items-center",
                    "gap-y-2",
                    "w-24",
                    "rounded-e-lg",
                    // "border",
                    // "border-s-2",
                    // "border-gray-300",
                    // "border-s-gray-100",
                    "bg-gradient-to-r from-violet-500 to-fuchsia-500", // TODO better background color
                    "p-2.5",
                    "text-lg",
                    "focus:outline-none",
                    "border-0",
                    "align-middle",
                    "justify-end",
                    "cursor-pointer",
                    "font-bold",
                )

                +"2.x.x"

                i {
                    classes = setOf("fa-solid", "fa-chevron-down", "pl-4", "text-xs", "align-middle")
                }
            }

            div {
                classes = setOf("hidden", "bg-search-bg", "absolute", "w-38", "py-1", "mt-1", "rounded-lg")

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
