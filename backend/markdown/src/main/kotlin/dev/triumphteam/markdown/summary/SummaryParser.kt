package dev.triumphteam.markdown.summary

import dev.triumphteam.markdown.summary.renderer.SummaryRenderer
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