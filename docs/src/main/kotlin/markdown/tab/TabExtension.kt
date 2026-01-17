package dev.triumphteam.website.docs.markdown.tab

import org.commonmark.parser.Parser

public class TabExtension : Parser.ParserExtension {

    public companion object {

        public fun create(): TabExtension = TabExtension()
    }

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(TabsBlockParser.Factory())
    }
}
