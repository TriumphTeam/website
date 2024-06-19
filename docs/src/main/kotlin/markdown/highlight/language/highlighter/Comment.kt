package dev.triumphteam.website.docs.markdown.highlight.language.highlighter

import dev.triumphteam.website.docs.markdown.highlight.GenericEnd
import dev.triumphteam.website.docs.markdown.highlight.GenericStart
import dev.triumphteam.website.docs.markdown.highlight.Highlight
import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.indicesOf
import dev.triumphteam.website.docs.markdown.highlight.language.INVALID_INDEX
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageHighlightComponent
import dev.triumphteam.website.docs.markdown.highlight.lengthToEOF

public class CommentHighlighter(private val delimeters: Set<String>) : LanguageHighlightComponent {

    override fun highlight(code: String): Set<Highlight> {
        return buildSet {
            delimeters.flatMap { code.indicesOf(it) }.forEach { start ->
                add(
                    Highlight(
                        start = GenericStart(start, HighlightType.COMMENT),
                        end = GenericEnd(start + code.lengthToEOF(start)),
                        type = HighlightType.COMMENT,
                    )
                )
            }
        }
    }
}

public class MultilineCommentHighlighter : LanguageHighlightComponent {

    private companion object {
        private const val SLASH = '/'
        private const val ASTERISK = '*'
    }

    override fun highlight(code: String): Set<Highlight> {
        var openerIndex = INVALID_INDEX

        var slashIndex = INVALID_INDEX
        var asteriskIndex = INVALID_INDEX

        fun resetAll() {
            openerIndex = INVALID_INDEX
            slashIndex = INVALID_INDEX
            asteriskIndex = INVALID_INDEX
        }

        return buildSet {
            code.forEachIndexed { index, char ->

                if (char == SLASH) {
                    slashIndex = index

                    // Previous was an asterisk, now it's a slash, we found a "closer"
                    if (asteriskIndex + 1 == index) {

                        // If there is no opener, we ignore it
                        if (openerIndex == INVALID_INDEX) return@forEachIndexed

                        add(
                            Highlight(
                                start = GenericStart(openerIndex, HighlightType.COMMENT),
                                end = GenericEnd(index + 1),
                                type = HighlightType.COMMENT,
                            )
                        )

                        resetAll()
                    }
                }

                if (char == ASTERISK) {
                    asteriskIndex = index

                    // Previous was a slash, no it's asterisk, we found an "opener"
                    if (slashIndex + 1 == index) {
                        openerIndex = slashIndex
                    }
                }
            }
        }
    }
}
