package dev.triumphteam.website.docs.markdown.content

import dev.triumphteam.website.docs.markdown.HeaderIdRenderer
import dev.triumphteam.website.project.ContentEntry
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text

public class ContentRenderer : AbstractVisitor() {

    private var writer = ContentWriter()

    public fun render(node: Node): List<ContentEntry> {
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
