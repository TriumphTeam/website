package dev.triumphteam.website.docs.markdown

import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import org.commonmark.ext.task.list.items.TaskListItemMarker
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
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext

public class MarkdownRenderer(private val context: HtmlNodeRendererContext) : AbstractVisitor(), NodeRenderer {

    private val html = context.writer

    private var hashHref = StringBuilder()
    private var hash = false

    private val sections = mutableMapOf<Int, String>()

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
        sections.forEach { _ ->
            html.tag("/section")
            html.line()
        }
    }

    override fun visit(heading: Heading) {
        val level = heading.level + 1

        if (sections.contains(level)) {
            val tags = sections.filterKeys { it >= level }
            tags.forEach { _ ->
                html.tag("/section")
                html.line()
            }
            val iterator = sections.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key in tags) iterator.remove()
            }
        }

        val parentId = sections[level - 1]
        val currentId = buildString {
            if (parentId != null) append(parentId).append("-")
            append(HeaderIdRenderer().render(heading))
        }
        html.tag("section", mapOf("id" to currentId))
        sections[level] = currentId

        val tag = "h${level}"
        html.line()
        html.tag(tag)
        appendHash(currentId)
        visitChildren(heading)
        html.tag("/$tag")
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
        val attributes = getAttrs(bulletList, "ul")
        if (checkTaskList(bulletList)) attributes["class"] = "task-list"
        renderListBlock(bulletList, "ul", attributes)
    }

    override fun visit(fencedCodeBlock: FencedCodeBlock) {
        renderCodeBlock(fencedCodeBlock.literal, fencedCodeBlock, (fencedCodeBlock.info ?: "").trim())
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
        renderCodeBlock(indentedCodeBlock.literal, indentedCodeBlock)
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
        if (link.title != null) attrs["title"] = link.title
        if (!url.startsWith("/")) {
            attrs["target"] = "_blank"
        }

        html.tag("a", getAttrs(link, "a", attrs))
        visitChildren(link)
        html.tag("/a")
    }

    override fun visit(image: Image) {
        var url = image.destination

        val attrs: MutableMap<String, String> = java.util.LinkedHashMap()
        if (context.shouldSanitizeUrls()) {
            url = context.urlSanitizer().sanitizeImageUrl(url)
        }

        attrs["src"] = context.encodeUrl(url)
        if (image.title != null) {
            attrs["title"] = image.title
        }

        html.tag("img", getAttrs(image, "img", attrs), true)
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
        if (hash) {
            hashHref.append(text.literal)
            return
        }

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

    private fun renderCodeBlock(literal: String, node: Node, language: String? = null) {
        html.line()
        html.tag("div", mapOf("id" to "code"))
        html.tag("pre", getAttrs(node, "pre"))
        appendCopy()

        // Grab language definition to highlight the code
        val languageDefinition = LanguageDefinition.fromString(language)
        val attributes = when (languageDefinition) {
            is LanguageDefinition.Empty -> emptyMap()
            else -> mapOf("class" to "lang-${languageDefinition.name}")
        }

        html.tag("code", getAttrs(node, "code", attributes))

        // Highlight before appending
        // It's appended as raw; highlighter will escape html already
        html.raw(languageDefinition.highlightCode(literal))

        html.tag("/code")
        html.tag("/pre")
        html.tag("/div")
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

    private tailrec fun checkTaskList(node: Node): Boolean {
        val firstChild = node.firstChild ?: return false
        if (firstChild is TaskListItemMarker) return true
        return checkTaskList(firstChild)
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

    private fun getAttrs(node: Node, tagName: String): MutableMap<String, String> {
        return getAttrs(node, tagName, emptyMap())
    }

    private fun getAttrs(
        node: Node,
        tagName: String,
        defaultAttributes: Map<String, String>,
    ): MutableMap<String, String> {
        return context.extendAttributes(node, tagName, defaultAttributes)
    }

    private fun appendHash(href: String) {
        html.tag("a", mapOf("id" to "hash", "href" to "#$href"))
        html.text("#")
        html.tag("/a")
    }

    private fun appendCopy() {
        html.raw(
            """
                <svg class="copy-icon" id="copy" aria-hidden="true" focusable="false" data-prefix="far" data-icon="clone" class="svg-inline--fa fa-clone fa-w-16"
                     role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
                    <path fill="currentColor"
                          d="M464 0H144c-26.51 0-48 21.49-48 48v48H48c-26.51 0-48 21.49-48 48v320c0 26.51 21.49 48 48 48h320c26.51 0 48-21.49 48-48v-48h48c26.51 0 48-21.49 48-48V48c0-26.51-21.49-48-48-48zM362 464H54a6 6 0 0 1-6-6V150a6 6 0 0 1 6-6h42v224c0 26.51 21.49 48 48 48h224v42a6 6 0 0 1-6 6zm96-96H150a6 6 0 0 1-6-6V54a6 6 0 0 1 6-6h308a6 6 0 0 1 6 6v308a6 6 0 0 1-6 6z"></path>
                </svg>
            """.trimIndent()
        )
    }

}
