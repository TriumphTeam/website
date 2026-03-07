package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.a
import dev.triumphteam.horizon.html.attributes.Target
import dev.triumphteam.horizon.html.element.ATag

private const val BUTTON_CLASSES =
    "flex items-center justify-center gap-2 rounded-lg border border-white/10 px-4 font-medium transition select-none"

public fun FlowContent.barButton(
    decorate: String = "bg-white/5 hover:bg-white/10 text-dark-text-primary text-sm",
    tooltip: String? = null,
    link: String? = null,
    small: Boolean = false,
    children: ATag.() -> Unit,
) {

    val size = if (small) "" else "h-11"

    a(
        href = link,
        target = Target.BLANK,
        rel = "noopener noreferrer",
        className = "$BUTTON_CLASSES $size $decorate",
        attributes = if (tooltip != null) mutableMapOf("data-tooltip" to tooltip) else mutableMapOf(),
    ) {
        children()
    }
}
