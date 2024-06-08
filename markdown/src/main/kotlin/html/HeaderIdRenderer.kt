package dev.triumphteam.markdown.html

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text

class HeaderIdRenderer : AbstractVisitor() {

    private val texts = mutableListOf<String>()

    fun render(node: Node): String {
        node.accept(this)
        return texts.joinToString("-") { it.replace(" ", "-") }.lowercase()
    }

    override fun visit(heading: Heading) {
        visitChildren(heading)
    }

    override fun visit(text: Text) {
        texts.add(text.literal)
    }

}