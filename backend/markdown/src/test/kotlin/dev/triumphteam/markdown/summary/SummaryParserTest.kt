package dev.triumphteam.markdown.summary

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SummaryParserTest {

    private val parser = SummaryRenderer()

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
                Item("Entry 1", "destination.md", 0),
                Item("Entry 2", "destination.md", 0),
                Item("Entry 3", "destination.md", 0),
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
                Item("Entry 1", "destination.md", 0),
                Item("Entry 2", "destination.md", 0),
                Header("Header"),
                Item("Entry 3", "destination.md", 0),
                Item("Entry 4", "destination.md", 0),
            )
        )
    }

}