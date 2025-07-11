package dev.triumphteam.website.docs.markdown.placeholder

import dev.triumphteam.website.docs.markdown.tab.TabBlockParser
import dev.triumphteam.website.docs.markdown.tab.TabsBlockParser
import org.commonmark.parser.Parser

public class PlaceholderExtension : Parser.ParserExtension {

    public companion object {

        public fun create(): PlaceholderExtension = PlaceholderExtension()
    }

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customInlineContentParserFactory(PlaceholderParser.Factory())
    }
}