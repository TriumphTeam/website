package dev.triumphteam.markdown.hint

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class HintExtension : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(HintBlockParser.Factory())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { HintNodeRenderer(it) }
    }

}