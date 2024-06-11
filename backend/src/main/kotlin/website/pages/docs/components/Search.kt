package dev.triumphteam.backend.website.pages.docs.components

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.input

public fun FlowContent.search() {
    div {
        classes = setOf("flex", "items-center", "max-w-md", "mx-auto", "bg-search-bg", "rounded-lg")

        div {
            classes = setOf("w-full")

            input {
                type = InputType.search
                disabled = true
                classes =
                    setOf("w-full", "px-4", "py-1", "text-white", "rounded-full", "bg-search-bg", "focus:outline-none")
                placeholder = "Search"
            }
        }

        div {
            button {
                type = ButtonType.submit
                // Enable after
                disabled = true
                classes = setOf(
                    "flex",
                    "items-center",
                    "bg-primary",
                    "justify-center",
                    "w-12",
                    "h-12",
                    "text-white",
                    "rounded-r-lg",
                    "hover:scale-105",
                    "transition ease-in-out delay-100",
                )

                i {
                    classes = setOf("w-5", "h-5", "fa-solid", "fa-magnifying-glass")
                }
            }
        }
    }
}
