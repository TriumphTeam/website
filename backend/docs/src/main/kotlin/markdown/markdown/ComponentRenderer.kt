package dev.triumphteam.website.docs.markdown.markdown

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
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.renderer.NodeRenderer

public class ComponentRenderer() : AbstractVisitor(), NodeRenderer {

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

    }

    override fun visit(paragraph: Paragraph) {

    }

    override fun visit(blockQuote: BlockQuote) {

    }

    override fun visit(bulletList: BulletList) {

    }

    override fun visit(fencedCodeBlock: FencedCodeBlock) {

    }

    override fun visit(htmlBlock: HtmlBlock) {

    }

    override fun visit(thematicBreak: ThematicBreak) {

    }

    override fun visit(indentedCodeBlock: IndentedCodeBlock) {

    }

    override fun visit(link: Link) {

    }

    override fun visit(image: Image) {

    }

    override fun visit(listItem: ListItem) {

    }

    override fun visit(orderedList: OrderedList) {

    }

    override fun visit(emphasis: Emphasis) {

    }

    override fun visit(strongEmphasis: StrongEmphasis) {

    }

    override fun visit(text: Text) {

    }

    override fun visit(code: Code) {

    }

    override fun visit(htmlInline: HtmlInline) {

    }

    override fun visit(softLineBreak: SoftLineBreak) {

    }

    override fun visit(hardLineBreak: HardLineBreak) {

    }

    override fun visitChildren(parent: Node) {

    }
}
