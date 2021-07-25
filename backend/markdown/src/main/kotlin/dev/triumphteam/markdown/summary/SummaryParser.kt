package dev.triumphteam.markdown.summary

import dev.triumphteam.markdown.summary.renderer.SummaryRenderer
import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import org.commonmark.parser.Parser

class SummaryParser {

    private val mdParser = Parser.builder().build()
    private val renderer = SummaryRenderer()

    fun parse(md: String): List<Entry> {
        val document = mdParser.parse(md)
        renderer.render(document)
        return renderer.finalize()
    }

    companion object Feature : ApplicationFeature<Application, SummaryParser, SummaryParser> {

        override val key: AttributeKey<SummaryParser> = AttributeKey("SummaryParser")

        override fun install(pipeline: Application, configure: SummaryParser.() -> Unit): SummaryParser {
            return SummaryParser()
        }
    }

}