package dev.triumphteam.website.docs.markdown.highlight.language.highlighter

import dev.triumphteam.website.docs.markdown.highlight.GenericEnd
import dev.triumphteam.website.docs.markdown.highlight.GenericStart
import dev.triumphteam.website.docs.markdown.highlight.Highlight
import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.isNewLine
import dev.triumphteam.website.docs.markdown.highlight.language.INVALID_INDEX
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageHighlightComponent

public class StringHighlighter(
    private val delimeters: Set<Char>,
    private val escapeChar: Char,
) : LanguageHighlightComponent {

    override fun highlight(code: String): Set<Highlight> {
        return delimeters.flatMap { delimeter ->
            highlightString(code, delimeter)
        }.toSet()
    }

    private fun highlightString(code: String, delimeter: Char): Set<Highlight> {
        var escaped = false
        var openerIndex = INVALID_INDEX

        return buildSet {
            code.forEachIndexed { index, char ->

                if (char == escapeChar) {
                    escaped = true
                    return@forEachIndexed
                }

                if (char == delimeter) {
                    // Skip if escaped
                    if (escaped) return@forEachIndexed

                    // Found opener delimiter
                    if (openerIndex == INVALID_INDEX) {
                        openerIndex = index
                        return@forEachIndexed
                    }

                    // Found both so close them
                    add(
                        Highlight(
                            start = GenericStart(openerIndex, HighlightType.STRING),
                            end = GenericEnd(index + 1),
                            type = HighlightType.STRING,
                        )
                    )

                    // Reset search
                    openerIndex = INVALID_INDEX
                    return@forEachIndexed
                }

                // If we find the end of line reset since it's not allowed
                if (char.isNewLine()) {
                    openerIndex = INVALID_INDEX
                }

                // Always remove escaped if nothing was found
                escaped = false
            }
        }
    }
}
