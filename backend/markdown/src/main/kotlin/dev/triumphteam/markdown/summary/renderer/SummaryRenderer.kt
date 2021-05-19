package dev.triumphteam.markdown.summary.renderer

import dev.triumphteam.markdown.summary.Entry
import dev.triumphteam.markdown.summary.writer.SummaryWriter
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.BulletList
import org.commonmark.node.Document
import org.commonmark.node.Heading
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.DefaultUrlSanitizer

class SummaryRenderer : AbstractVisitor(), NodeRenderer {

    private val urlSanitizer = DefaultUrlSanitizer()
    private var writer = SummaryWriter()

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf(
            Document::class.java,
            Heading::class.java,
            BulletList::class.java,
            Link::class.java,
            ListItem::class.java,
            Text::class.java,
        )
    }

    override fun render(node: Node) {
        node.accept(this)
    }

    override fun visit(heading: Heading) {
        writer.openHeader()
        visitChildren(heading)
        writer.closeHeader()
    }

    override fun visit(bulletList: BulletList) {
        writer.openUl()
        visitChildren(bulletList)
        writer.closeUl()
    }

    override fun visit(link: Link) {
        writer.openLink(urlSanitizer.sanitizeLinkUrl(link.destination))
        visitChildren(link)
        writer.closeLink()
    }

    override fun visit(listItem: ListItem) {
        writer.openLi()
        visitChildren(listItem)
        writer.closeLi()
    }

    override fun visit(text: Text) {
        writer.append(text.literal)
    }

    fun finalize(): List<Entry> {
        return writer.build().also { writer = SummaryWriter() }
    }

}