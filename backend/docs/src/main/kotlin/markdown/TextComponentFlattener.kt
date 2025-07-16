package dev.triumphteam.website.docs.markdown

import dev.triumphteam.website.serializable.DocComponent
import dev.triumphteam.website.serializable.WithChildren

public fun flatten(separator: String, component: List<DocComponent>): String {
    return component.joinToString(separator) { flatten(separator, it) }
}

private fun flatten(separator: String, component: DocComponent): String {
    return when (component) {
        is DocComponent.Text -> component.content
        is WithChildren -> flatten(separator, component.children)
        else -> ""
    }
}