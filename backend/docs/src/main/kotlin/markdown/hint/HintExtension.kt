package dev.triumphteam.website.docs.markdown.hint

import org.commonmark.parser.Parser

public class HintExtension : Parser.ParserExtension {

    public companion object {

        public fun create(): HintExtension = HintExtension()
    }

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(HintBlockParser.Factory())
    }
}
