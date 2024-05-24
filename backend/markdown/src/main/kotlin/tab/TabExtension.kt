package dev.triumphteam.markdown.tab

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class TabExtension : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(TabsBlockParser.Factory())
        parserBuilder.customBlockParserFactory(TabBlockParser.Factory())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { TabNodeRenderer(it) }
    }

}