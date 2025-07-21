package dev.triumphteam.website.docs.markdown

import ComponentChildren
import DocComponent
import TextComponent
import WithChildren

public fun flatten(separator: String, component: ComponentChildren): String {
    return component.children.joinToString(separator) { flatten(separator, it) }
}

private fun flatten(separator: String, component: DocComponent): String {
    return when (component) {
        is TextComponent -> component.content
        is WithChildren -> flatten(separator, component.children)
        else -> ""
    }
}