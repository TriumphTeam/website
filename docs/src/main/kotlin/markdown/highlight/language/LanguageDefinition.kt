package dev.triumphteam.website.docs.markdown.highlight.language

import dev.triumphteam.website.docs.markdown.highlight.GenericStart
import dev.triumphteam.website.docs.markdown.highlight.Highlight
import dev.triumphteam.website.docs.markdown.highlight.HighlightStep
import dev.triumphteam.website.docs.markdown.highlight.HighlightType

public abstract class LanguageDefinition(
    private val components: List<LanguageHighlightComponent>,
    private val globalValidator: List<HighlightValidator> = emptyList(),
    private val stepValidator: List<StepValidator> = emptyList(),
    private val allowDuplicate: Boolean = false,
) {

    public fun captureHighlights(code: String): Map<Int, Collection<HighlightStep>> {
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
}
