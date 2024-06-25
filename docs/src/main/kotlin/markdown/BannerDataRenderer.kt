package dev.triumphteam.website.docs.markdown

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.HtmlBlock
import org.commonmark.node.Node

private val headerPattern = "(?<=<center><h1>)[^<]+(?=</h1></center>)".toRegex()
private val paragraphPattern = "(?<=<center><p>)[^<]+(?=</p></center>)".toRegex()

public class BannerDataRenderer : AbstractVisitor() {

    private var header: String? = null
    private var paragraph: String? = null

    public fun render(node: Node): Pair<String?, String?> {
        node.accept(this)

        return header to paragraph
    }

    override fun visit(block: HtmlBlock) {
        val literal = block.literal

        val headerMatch = headerPattern.find(literal)
        if (headerMatch != null) {
            if (header != null) return
            header = headerMatch.value
            return
        }

        val paragraphMatch = headerPattern.find(literal) ?: return
        if (paragraph != null) return
        paragraph = paragraphMatch.value
    }
}
