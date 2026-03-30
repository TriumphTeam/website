package dev.triumphteam.backend.content

import dev.triumphteam.website.serializable.CodeComponent
import dev.triumphteam.website.serializable.ContentSection
import dev.triumphteam.website.serializable.DocComponent
import dev.triumphteam.website.serializable.HardLineBreakComponent
import dev.triumphteam.website.serializable.HeaderComponent
import dev.triumphteam.website.serializable.RootComponent
import dev.triumphteam.website.serializable.SoftLineBreakComponent
import dev.triumphteam.website.serializable.TextComponent
import dev.triumphteam.website.serializable.WithChildren

public class ContentExtractor(
    private val root: RootComponent,
    private val sectionCreator: (HeaderComponent, String) -> ContentSection,
) {

    private val sections = mutableListOf<ContentSection>()

    private var current: Builder? = null

    public fun extract(): List<ContentSection> {
        root.children.forEach { component ->
            extractFromComponent(component)
        }

        val current = current
        if (current != null) {
            sections += current.build()
        }

        return sections.toList()
    }

    private fun extractFromComponent(component: DocComponent) {
        if (component is HeaderComponent && component.level <= 1) {
            val current = current
            if (current != null) {
                sections += current.build()
            }

            this.current = Builder(component)
            return
        }

        if (component is TextComponent) {
            current?.append(component.content)
            return
        }

        if (component is CodeComponent) {
            current?.append(component.content)
            return
        }

        if (component is SoftLineBreakComponent || component is HardLineBreakComponent) {
            current?.append(" ")
            return
        }

        if (component !is WithChildren) return

        if (component.children.isEmpty()) return

        component.children.forEach { child ->
            extractFromComponent(child)
        }
    }

    private inner class Builder(val header: HeaderComponent) {
        private val contentBuilder = StringBuilder()

        fun append(text: String) {
            contentBuilder.append(text)
        }

        fun build(): ContentSection {
            return sectionCreator(header, contentBuilder.toString().trimIndent())
        }
    }
}
