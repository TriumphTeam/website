package dev.triumphteam.markdown

import dev.triumphteam.markdown.renderer.SummaryRenderer
import dev.triumphteam.markdown.writer.Entry
import org.commonmark.parser.Parser

object SummaryParser {

    private val mdParser = Parser.builder().build()
    private val renderer = SummaryRenderer()

    fun parse(md: String): List<Entry> {
        val document = mdParser.parse(md)
        renderer.render(document)
        return renderer.finalize()
    }

}