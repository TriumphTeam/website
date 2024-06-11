package dev.triumphteam.website.docs.markdown.tab

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

public class TabExtension : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    public companion object {

        public fun create(): TabExtension = TabExtension()
    }

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customBlockParserFactory(TabsBlockParser.Factory())
        parserBuilder.customBlockParserFactory(TabBlockParser.Factory())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { TabNodeRenderer(it) }
    }

}
