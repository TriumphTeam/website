package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.hr

public fun FlowContent.separator() {
    hr(className = "!p-0 my-4 h-px border-t-0 bg-transparent bg-gradient-to-r from-transparent to-transparent opacity-25 via-neutral-400")
}
