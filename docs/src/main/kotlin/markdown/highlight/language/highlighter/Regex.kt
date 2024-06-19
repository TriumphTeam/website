package dev.triumphteam.website.docs.markdown.highlight.language.highlighter

import dev.triumphteam.website.docs.markdown.highlight.GenericEnd
import dev.triumphteam.website.docs.markdown.highlight.GenericStart
import dev.triumphteam.website.docs.markdown.highlight.Highlight
import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.indicesOf
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageHighlightComponent

public class RegexHighlighter(
    private val type: HighlightType,
    private val expressions: List<Regex>,
) : LanguageHighlightComponent {

    override fun highlight(code: String): Collection<Highlight> {
        return expressions.flatMap { regex ->
            code.indicesOf(regex).map { index ->
                Highlight(
                    start = GenericStart(index.first, type),
                    end = GenericEnd(index.last + 1),
                    type = type,
                )
            }
        }
    }
}
