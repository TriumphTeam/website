package dev.triumphteam.markdown

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Document
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListBlock
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlRenderer


class LaterRenderer(private val context: HtmlNodeRendererContext) : AbstractVisitor(), NodeRenderer {

    private val html = context.writer

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(
            Document::class.java,
            Heading::class.java,
            Paragraph::class.java,
            BlockQuote::class.java,
            BulletList::class.java,
            FencedCodeBlock::class.java,
            HtmlBlock::class.java,
            ThematicBreak::class.java,
            IndentedCodeBlock::class.java,
            Link::class.java,
            ListItem::class.java,
            OrderedList::class.java,
            Image::class.java,
            Emphasis::class.java,
            StrongEmphasis::class.java,
            Text::class.java,
            Code::class.java,
            HtmlInline::class.java,
            SoftLineBreak::class.java,
            HardLineBreak::class.java
        )
    }

    override fun render(node: Node) {
        node.accept(this)
    }

    override fun visit(document: Document) {
        // No rendering itself
        visitChildren(document)
    }

    override fun visit(heading: Heading) {
        val htag = "h" + heading.level
        html.line()
        html.tag(htag, getAttrs(heading, htag))
        visitChildren(heading)
        html.tag("/$htag")
        html.line()
    }

    override fun visit(paragraph: Paragraph) {
        val inTightList = isInTightList(paragraph)
        if (!inTightList) {
            html.line()
            html.tag("p", getAttrs(paragraph, "p"))
        }
        visitChildren(paragraph)
        if (!inTightList) {
            html.tag("/p")
            html.line()
        }
    }

    override fun visit(blockQuote: BlockQuote) {
        html.line()
        html.tag("blockquote", getAttrs(blockQuote, "blockquote"))
        html.line()
        visitChildren(blockQuote)
        html.line()
        html.tag("/blockquote")
        html.line()
    }

    override fun visit(bulletList: BulletList) {
        renderListBlock(bulletList, "ul", getAttrs(bulletList, "ul"))
    }

    override fun visit(fencedCodeBlock: FencedCodeBlock) {
        val literal = fencedCodeBlock.literal
        val attributes: MutableMap<String, String> = LinkedHashMap()
        val info = fencedCodeBlock.info
        if (info != null && info.isNotEmpty()) {
            val space = info.indexOf(" ")
            val language = if (space == -1) {
                info
            } else {
                info.substring(0, space)
            }
            attributes["class"] = "language-$language"
        }
        renderCodeBlock(literal, fencedCodeBlock, attributes)
    }

    override fun visit(htmlBlock: HtmlBlock) {
        html.line()
        if (context.shouldEscapeHtml()) {
            html.tag("p", getAttrs(htmlBlock, "p"))
            html.text(htmlBlock.literal)
            html.tag("/p")
        } else {
            html.raw(htmlBlock.literal)
        }
        html.line()
    }

    override fun visit(thematicBreak: ThematicBreak) {
        html.line()
        html.tag("hr", getAttrs(thematicBreak, "hr"), true)
        html.line()
    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock) {
        renderCodeBlock(indentedCodeBlock.literal, indentedCodeBlock, emptyMap())
    }

    override fun visit(link: Link) {
        val attrs: MutableMap<String, String> = LinkedHashMap()
        var url = link.destination
        if (context.shouldSanitizeUrls()) {
            url = context.urlSanitizer().sanitizeLinkUrl(url)
            attrs["rel"] = "nofollow"
        }
        url = context.encodeUrl(url)
        attrs["href"] = url
        if (link.title != null) {
            attrs["title"] = link.title
        }
        html.tag("a", getAttrs(link, "a", attrs))
        visitChildren(link)
        html.tag("/a")
    }

    override fun visit(listItem: ListItem) {
        html.tag("li", getAttrs(listItem, "li"))
        visitChildren(listItem)
        html.tag("/li")
        html.line()
    }

    override fun visit(orderedList: OrderedList) {
        val start = orderedList.startNumber
        val attrs: MutableMap<String, String> = LinkedHashMap()
        if (start != 1) {
            attrs["start"] = start.toString()
        }
        renderListBlock(orderedList, "ol", getAttrs(orderedList, "ol", attrs))
    }

    override fun visit(emphasis: Emphasis) {
        html.tag("em", getAttrs(emphasis, "em"))
        visitChildren(emphasis)
        html.tag("/em")
    }

    override fun visit(strongEmphasis: StrongEmphasis) {
        html.tag("strong", getAttrs(strongEmphasis, "strong"))
        visitChildren(strongEmphasis)
        html.tag("/strong")
    }

    override fun visit(text: Text) {
        html.text(text.literal)
    }

    override fun visit(code: Code) {
        html.tag("code", getAttrs(code, "code"))
        html.text(code.literal)
        html.tag("/code")
    }

    override fun visit(htmlInline: HtmlInline) {
        if (context.shouldEscapeHtml()) {
            html.text(htmlInline.literal)
        } else {
            html.raw(htmlInline.literal)
        }
    }

    override fun visit(softLineBreak: SoftLineBreak) {
        html.raw(context.softbreak)
    }

    override fun visit(hardLineBreak: HardLineBreak) {
        html.tag("br", getAttrs(hardLineBreak, "br"), true)
        html.line()
    }

    override fun visitChildren(parent: Node) {
        var node = parent.firstChild
        while (node != null) {
            val next = node.next
            context.render(node)
            node = next
        }
    }

    private fun renderCodeBlock(literal: String, node: Node, attributes: Map<String, String>) {
        html.line()
        html.tag("pre", getAttrs(node, "pre"))
        html.tag("code", getAttrs(node, "code", attributes))
        html.text(literal)
        html.tag("/code")
        html.tag("/pre")
        html.line()
    }

    private fun renderListBlock(listBlock: ListBlock, tagName: String, attributes: Map<String, String>) {
        html.line()
        html.tag(tagName, attributes)
        html.line()
        visitChildren(listBlock)
        html.line()
        html.tag("/$tagName")
        html.line()
    }

    private fun isInTightList(paragraph: Paragraph): Boolean {
        val parent: Node? = paragraph.parent
        if (parent != null) {
            val gramps = parent.parent
            if (gramps is ListBlock) {
                return gramps.isTight
            }
        }
        return false
    }

    private fun getAttrs(node: Node, tagName: String): Map<String, String> {
        return getAttrs(node, tagName, emptyMap())
    }

    private fun getAttrs(node: Node, tagName: String, defaultAttributes: Map<String, String>): Map<String, String> {
        return context.extendAttributes(node, tagName, defaultAttributes)
    }

}

fun main() {
    val md = """
        # Hello
        
        ```java
            public static String TEST = "Hey!";
        ```
    """.trimIndent()

    val parser: Parser = Parser.builder().build()
    val document: Node = parser.parse(md)
    val renderer = HtmlRenderer.builder().build()
    println(renderer.render(document))
}