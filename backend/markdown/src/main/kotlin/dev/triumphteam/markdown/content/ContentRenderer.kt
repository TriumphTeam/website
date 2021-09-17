package dev.triumphteam.markdown.content

import dev.triumphteam.markdown.html.HeaderIdRenderer
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.Parser

class ContentRenderer : AbstractVisitor() {

    private var writer = ContentWriter()

    fun render(node: Node): List<ContentEntry> {
        node.accept(this)
        return finalize()
    }

    override fun visit(heading: Heading) {
        writer.openHeader(heading.level, HeaderIdRenderer().render(heading))
        visitChildren(heading)
        writer.closeHeader()
    }

    override fun visit(text: Text) {
        writer.append(text.literal)
    }

    private fun finalize(): List<ContentEntry> {
        return writer.build().also { writer = ContentWriter() }
    }

}

fun main() {
    val parser = Parser.builder().build()
    val markdown = parser.parse(
        """
            # Header
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.

            ## Sub header
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            
            ### Test
            Boyyyyy

            # Header2
            Hello

            ## Sub2
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.

            # Header 3
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
        """.trimIndent()
    )
    println(ContentRenderer().render(markdown))
}