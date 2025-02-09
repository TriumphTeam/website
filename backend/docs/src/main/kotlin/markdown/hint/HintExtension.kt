package dev.triumphteam.website.docs.markdown.hint

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

public class HintExtension : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    public companion object {

        public fun create(): HintExtension = HintExtension()
    }

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(HintBlockParser.Factory())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { HintNodeRenderer(it) }
    }
}
