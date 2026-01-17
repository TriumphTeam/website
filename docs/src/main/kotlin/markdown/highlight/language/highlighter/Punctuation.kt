package dev.triumphteam.website.docs.markdown.highlight.language.highlighter

import dev.triumphteam.website.docs.markdown.highlight.GenericEnd
import dev.triumphteam.website.docs.markdown.highlight.GenericStart
import dev.triumphteam.website.docs.markdown.highlight.Highlight
import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.indicesOf
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageHighlightComponent

public class PunctuationHighlighter(private val punctuations: List<Char>) : LanguageHighlightComponent {

    override fun highlight(code: String): Collection<Highlight> {
        return punctuations.flatMap { punctuation ->
            code.indicesOf(punctuation).map { index ->
                Highlight(
                    start = GenericStart(index, HighlightType.PUNCTUATION),
                    end = GenericEnd(index + 1),
                    type = HighlightType.PUNCTUATION,
                )
            }
        }
    }
}
