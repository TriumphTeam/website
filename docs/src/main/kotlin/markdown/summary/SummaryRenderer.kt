package dev.triumphteam.website.docs.markdown.summary

import dev.triumphteam.website.project.SummaryEntry
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node

public class SummaryRenderer : AbstractVisitor() {

    private var writer = SummaryWriter()

    public fun render(node: Node): List<SummaryEntry> {
        node.accept(this)
        return finalize()
    }

    override fun visit(heading: Heading) {
        writer.append(heading)
    }

    private fun finalize(): List<SummaryEntry> {
        return writer.build().also { writer = SummaryWriter() }
    }

}
