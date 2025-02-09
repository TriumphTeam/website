package dev.triumphteam.website.docs.markdown.highlight.language

import dev.triumphteam.website.docs.markdown.highlight.GenericStart
import dev.triumphteam.website.docs.markdown.highlight.Highlight
import dev.triumphteam.website.docs.markdown.highlight.HighlightStep
import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.escapeHtml
import dev.triumphteam.website.docs.markdown.highlight.language.languages.GroovyLanguage
import dev.triumphteam.website.docs.markdown.highlight.language.languages.JavaLanguage
import dev.triumphteam.website.docs.markdown.highlight.language.languages.KotlinLanguage
import dev.triumphteam.website.docs.markdown.highlight.language.languages.XmlLanguage

public abstract class LanguageDefinition(
    public val name: String = "",
    private val components: List<LanguageHighlightComponent> = emptyList(),
    private val globalValidator: List<HighlightValidator> = emptyList(),
    private val stepValidator: List<StepValidator> = emptyList(),
    private val allowDuplicate: Boolean = false,
) {

    public companion object {

        public fun fromString(language: String?): LanguageDefinition = when (language) {
            "kt", "kotlin" -> KotlinLanguage
            "java" -> JavaLanguage
            "groovy" -> GroovyLanguage
            "xml" -> XmlLanguage
            else -> Empty
        }
    }

    private fun captureHighlights(code: String): Map<Int, Collection<HighlightStep>> {
        val highlights = components.flatMap { it.highlight(code) }.toMutableList()

        globalValidator.forEach { validator ->
            val iterator = highlights.iterator()

            while (iterator.hasNext()) {
                val highlight = iterator.next()
                if (validator.shouldRemove(highlight, highlights)) {
                    iterator.remove()
                }
            }
        }

        return highlights.flatMap(Highlight::steps).groupBy(HighlightStep::index).mapValues { (_, list) ->
            // Make sure it always starts with a closing instruction and removes repeats
            val values = list.sortedBy { it is GenericStart }.toMutableList()

            // Validate group
            stepValidator.forEach { validator ->
                val iterator = values.iterator()
                while (iterator.hasNext()) {
                    val highlight = iterator.next()
                    if (validator.shouldRemove(highlight, values)) {
                        iterator.remove()
                    }
                }
            }

            if (allowDuplicate) values else values.toSet()
        }
    }

    public fun highlightCode(code: String): String {
        val highlights = captureHighlights(code)
        return buildString {
            code.forEachIndexed { index, char ->
                val steps = highlights[index]
                steps?.forEach { append(it.createTag()) }
                append(char.escapeHtml())
            }
            highlights.filterKeys { it >= code.length }.forEach { (_, step) ->
                step.forEach { append(it.createTag()) }
            }
        }
    }

    public data object Empty : LanguageDefinition()
}

public sealed interface HighlightValidator {

    public fun shouldRemove(current: Highlight, highlights: List<Highlight>): Boolean

    public data object InComment : HighlightValidator {

        override fun shouldRemove(current: Highlight, highlights: List<Highlight>): Boolean {
            // Filter for only comments
            val comments = highlights.filter { it.type == HighlightType.COMMENT }
            // Check if current is within a comment
            return comments.any { current in it }
        }
    }

    public data object InString : HighlightValidator {

        override fun shouldRemove(current: Highlight, highlights: List<Highlight>): Boolean {
            if (current.type == HighlightType.STRING) return false
            // Filter for only string
            val strings = highlights.filter { it.type == HighlightType.STRING }
            // Check if current is within a string
            return strings.any { current in it }
        }
    }

    public data object InAnnotation : HighlightValidator {

        override fun shouldRemove(current: Highlight, highlights: List<Highlight>): Boolean {
            if (current.type == HighlightType.ANNOTATION) return false
            // Filter for only annotations
            val annotations = highlights.filter { it.type == HighlightType.ANNOTATION }
            // Check if current is within a string
            return annotations.any { current in it }
        }
    }
}

public sealed interface StepValidator {

    public fun shouldRemove(current: HighlightStep, steps: List<HighlightStep>): Boolean

    public data object AnnotationAndFunction : StepValidator {

        override fun shouldRemove(current: HighlightStep, steps: List<HighlightStep>): Boolean {
            return when (current.type) {
                HighlightType.ANNOTATION -> false
                HighlightType.END -> false
                else -> steps.any { it.type == HighlightType.ANNOTATION }
            }
        }
    }

    public data object TypeAndFunction : StepValidator {

        override fun shouldRemove(current: HighlightStep, steps: List<HighlightStep>): Boolean {
            return when (current.type) {
                HighlightType.TYPE -> false
                HighlightType.END -> false
                else -> steps.any { it.type == HighlightType.TYPE }
            }
        }
    }

    public data object KeywordAndFunction : StepValidator {

        override fun shouldRemove(current: HighlightStep, steps: List<HighlightStep>): Boolean {
            return when (current.type) {
                HighlightType.KEYWORD -> false
                HighlightType.END -> false
                else -> steps.any { it.type == HighlightType.KEYWORD }
            }
        }
    }
}
