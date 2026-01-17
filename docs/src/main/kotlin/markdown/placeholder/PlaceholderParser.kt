package dev.triumphteam.website.docs.markdown.placeholder

import org.commonmark.node.CustomNode
import org.commonmark.parser.beta.InlineContentParser
import org.commonmark.parser.beta.InlineContentParserFactory
import org.commonmark.parser.beta.InlineParserState
import org.commonmark.parser.beta.ParsedInline

public class PlaceholderParser : InlineContentParser {
    private companion object {
        private const val PLACEHOLDER_OPENER = '{'
        private const val PLACEHOLDER_CLOSER = '}'
    }

    override fun tryParse(inlineParserState: InlineParserState): ParsedInline? {
        val scanner = inlineParserState.scanner()
        scanner.next() // skip the first `{`;
        val textStart = scanner.position()

        // Could not find the closing character.
        if (scanner.find(PLACEHOLDER_CLOSER) <= 0) return ParsedInline.none()

        val textSource = scanner.getSource(textStart, scanner.position())
        val content = textSource.getContent()
        scanner.next() // Skip the final `}`.

        // No white spaces are allowed within the placeholder.
        if (content.any(Char::isWhitespace)) return ParsedInline.none()

        return ParsedInline.of(Placeholder(content), scanner.position())
    }

    public class Factory : InlineContentParserFactory {
        override fun getTriggerCharacters(): Set<Char> = setOf(PLACEHOLDER_OPENER)

        override fun create(): InlineContentParser {
            return PlaceholderParser()
        }
    }
}

public data class Placeholder(public val identifier: String) : CustomNode()
