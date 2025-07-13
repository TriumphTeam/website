package dev.triumphteam.website.docs.markdown

import dev.triumphteam.website.docs.DocComponent
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.hint.HintBlock
import dev.triumphteam.website.docs.markdown.placeholder.Placeholder
import dev.triumphteam.website.docs.project.Replacement
import org.commonmark.ext.gfm.strikethrough.Strikethrough
import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.CustomBlock
import org.commonmark.node.CustomNode
import org.commonmark.node.Document
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak

public class MarkdownRenderer(private val replacements: Map<String, Replacement>) {

    public fun render(node: Node): DocComponent {
        if (node !is Document) error("Root node must be a Document")
        return renderNode(node)
    }

    private fun renderNode(node: Node): DocComponent {
        val children = renderChildren(node)
        return when (node) {
            is Document -> DocComponent.Document(children)
            is BlockQuote -> DocComponent.Quote(children)
            is BulletList -> DocComponent.BulletList(children)
            is Code -> DocComponent.Code(node.literal, children)
            is CustomBlock -> renderCustomBlock(node, children)
            is CustomNode -> renderCustomNode(node, children)
            is Emphasis -> DocComponent.Italic(children)

            is FencedCodeBlock -> {
                val languageDefinition = LanguageDefinition.fromString(node.info)
                DocComponent.CodeBlock(
                    content = languageDefinition.highlightCode(node.literal),
                    children = children,
                )
            }

            is HardLineBreak -> DocComponent.HardLineBreak(children)
            is Heading -> DocComponent.Header(node.level, children)
            is HtmlBlock -> DocComponent.Html(node.literal, children)
            is HtmlInline -> DocComponent.Html(node.literal, children)
            is Image -> DocComponent.Image(node.destination, children)
            is Link -> DocComponent.Link(node.destination, node.title, children)
            is ListItem -> DocComponent.ListItem(children)
            is OrderedList -> DocComponent.OrderedList(children)
            is Paragraph -> DocComponent.Paragraph(children)
            is SoftLineBreak -> DocComponent.SoftLineBreak(children)
            is StrongEmphasis -> DocComponent.Bold(children)
            is Text -> DocComponent.Text(node.literal, children)
            is ThematicBreak -> DocComponent.Separator(children)
            else -> unsupportedNode(node)
        }
    }

    private fun renderCustomNode(node: CustomNode, children: List<DocComponent>): DocComponent {
        return when (node) {
            is Placeholder -> DocComponent.Text(node.identifier, children)
            is Strikethrough -> DocComponent.Strikethrough(children)
            else -> unsupportedNode(node)
        }
    }

    private fun renderCustomBlock(node: CustomBlock, children: List<DocComponent>): DocComponent {
        return when (node) {
            is HintBlock -> DocComponent.Hint(node.type, children)
            else -> unsupportedNode(node)
        }
    }

    private fun renderChildren(parent: Node): List<DocComponent> {
        return buildList {
            var node = parent.firstChild
            while (node != null) {
                // A subclass of this visitor might modify the node, resulting in getNext returning a different node or no
                // node after visiting it. So get the next node before visiting.
                val next = node.next
                this += renderNode(node)
                node = next
            }
        }.toList()
    }

    private fun unsupportedNode(node: Node): Nothing = error("Unsupported node type: ${node.javaClass.simpleName}")
}