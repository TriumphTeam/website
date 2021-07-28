package dev.triumphteam.markdown.summary

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SummaryParserTest {

    private val parser = SummaryParser()

    @Test
    fun `Test header`() {
        val md = """
            # Header
        """.trimIndent()
        assertThat(parser.parse(md)).isEqualTo(listOf(Header("Header")))
    }

    @Test
    fun `Test list`() {
        val md = """
            * [Entry 1](destination.md)
            * [Entry 2](destination.md)
            * [Entry 3](destination.md)
        """.trimIndent()
        assertThat(parser.parse(md)).isEqualTo(
            listOf(
                Link("Entry 1", "destination.md", 0),
                Link("Entry 2", "destination.md", 0),
                Link("Entry 3", "destination.md", 0),
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
        assertThat(parser.parse(md)).isEqualTo(
            listOf(
                Link("Entry 1", "destination.md", 0),
                Link("Entry 2", "destination.md", 0),
                Header("Header"),
                Link("Entry 3", "destination.md", 0),
                Link("Entry 4", "destination.md", 0),
            )
        )
    }

}