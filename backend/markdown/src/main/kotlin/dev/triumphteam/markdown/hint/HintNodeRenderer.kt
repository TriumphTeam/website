package dev.triumphteam.markdown.hint

import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext


class HintNodeRenderer(
    private val context: HtmlNodeRendererContext,
) : NodeRenderer {

    private var html = context.writer

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(HintBlockParser.HintBlock::class.java)
    }

    override fun render(node: Node) {
        if (node !is HintBlockParser.HintBlock) return

        html.line()
        html.tag("div", mapOf("class" to "hint hint-${node.type.name.lowercase()}"))
        html.tag("div", mapOf("class" to "hint-icon"))
        html.raw(node.type.icon.trimIndent())
        html.tag("/div")
        html.tag("div", mapOf("class" to "hint-content"))
        renderChildren(node)
        html.tag("/div")
        html.tag("/div")
        html.line()
    }

    private fun renderChildren(parent: Node) {
        var node = parent.firstChild
        while (node != null) {
            val next = node.next
            context.render(node)
            node = next
        }
    }

}