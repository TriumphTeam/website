package dev.triumphteam.frontend.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.Tag
import dev.triumphteam.horizon.html.TagMarker
import dev.triumphteam.horizon.html.button

@TagMarker
public fun FlowContent.simpleButton(children: FlowContent.() -> Unit): Tag {
    return button(className = "flex justify-center cursor-pointer text-2xl text-light-text dark:text-dark-text bg-light-accent hover:bg-light-accent/80 dark:bg-dark-accent dark:hover:bg-dark-accent/80 rounded-md p-4 hover:scale-110") {
        children()
    }
}
