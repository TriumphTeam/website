package dev.triumphteam.markdown.summary

import dev.triumphteam.markdown.SummaryParser
import dev.triumphteam.markdown.writer.Header
import dev.triumphteam.markdown.writer.Link
import dev.triumphteam.markdown.writer.Menu
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SummaryParserTest {

    @Test
    fun `Test header`() {
        val md = """
            # Header
        """.trimIndent()
        assertThat(SummaryParser.parse(md)).isEqualTo(listOf(Header("Header")))
    }

    @Test
    fun `Test list`() {
        val md = """
            * [Entry 1](destination.md)
            * [Entry 2](destination.md)
            * [Entry 3](destination.md)
        """.trimIndent()
        assertThat(SummaryParser.parse(md)).isEqualTo(
            listOf(
                Link("Entry 1", "destination.md"),
                Link("Entry 2", "destination.md"),
                Link("Entry 3", "destination.md"),
            )
        )
    }

    @Test
    fun `Test list header`() {
        val md = """
            * [Entry 1](destination.md)
            * [Entry 2](destination.md)
            
            # Header
            
            * [Entry 3](destination.md)
            * [Entry 4](destination.md)
        """.trimIndent()
        assertThat(SummaryParser.parse(md)).isEqualTo(
            listOf(
                Link("Entry 1", "destination.md"),
                Link("Entry 2", "destination.md"),
                Header("Header"),
                Link("Entry 3", "destination.md"),
                Link("Entry 4", "destination.md"),
            )
        )
    }

    @Test
    fun `Test menu`() {

        val md = """
            * [Entry 1](destination.md)
              * [Sub Entry 1](destination.md)
              * [Sub Entry 2](destination.md)
        """.trimIndent()
        assertThat(SummaryParser.parse(md)).isEqualTo(
            listOf(
                Menu(
                    Link("Entry 1", "destination.md"),
                    listOf(
                        Link("Sub Entry 1", "destination.md"),
                        Link("Sub Entry 2", "destination.md"),
                    )
                ),
            )
        )
    }

}