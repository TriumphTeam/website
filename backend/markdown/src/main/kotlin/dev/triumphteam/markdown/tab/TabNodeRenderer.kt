package dev.triumphteam.markdown.tab

import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext

private val ID_REGEX = "[^a-zA-Z0-9\\-_]".toRegex()

class TabNodeRenderer(
    private val context: HtmlNodeRendererContext,
) : NodeRenderer {

    private val html = context.writer

    private var tabCounter = 0
    private var tabsCounter = 0

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(TabsBlock::class.java, TabBlock::class.java)
    }

    override fun render(node: Node) {
        println(node)
        if (node is TabBlock) return renderTab(node)
        if (node is TabsBlock) return renderTabs(node)
    }

    private fun renderTabs(node: TabsBlock) {
        tabsCounter++

        html.line()
        html.tag("div", mapOf("class" to "content-tabs"))
        renderChildren(node)
        tabCounter = 0
        html.tag("/div")
        html.line()
    }

    private fun renderTab(node: TabBlock) {
        tabCounter++

        val tabName = node.text
        val nameId = tabName.lowercase().replace(' ', '-').replace(ID_REGEX, "")

        html.line()
        html.raw(getInput(nameId, tabCounter, tabsCounter))
        html.tag("label", mapOf("for" to "tab-$tabsCounter-$nameId-$tabCounter"))
        html.text(tabName)
        html.tag("/label")
        html.tag("div", mapOf("class" to "content-tab"))
        renderChildren(node)
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

private fun getInput(name: String, counter: Int, tabsCounter: Int): String {
    val checked = if (counter == 1) "checked" else ""
    return "<input type=\"radio\" id=\"tab-$tabsCounter-$name-$counter\" name=\"tab-$tabsCounter\" $checked>"
}