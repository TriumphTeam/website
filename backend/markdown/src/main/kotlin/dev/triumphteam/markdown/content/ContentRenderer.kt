package dev.triumphteam.markdown.content

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text

class ContentRenderer : AbstractVisitor() {

    private var writer = ContentWriter()

    fun render(node: Node): List<ContentEntry> {
        node.accept(this)
        return finalize()
    }

    override fun visit(heading: Heading) {
        writer.openHeader(heading.level)
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