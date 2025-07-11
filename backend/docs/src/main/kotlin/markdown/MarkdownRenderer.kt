package dev.triumphteam.website.docs.markdown

import dev.triumphteam.website.docs.DocComponent
import dev.triumphteam.website.docs.markdown.placeholder.Placeholder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.LinkReferenceDefinition
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak

public class MarkdownRenderer {

    public fun render(node: Node): String {
        if (node !is Document) error("Root node must be a Document")
        val node = accept(node)
        println(Json.encodeToString<DocComponent>(node))
        return "AAAAH"
    }

    private fun accept(node: Node): DocComponent {
        val children = visitChildren(node)
        return when (node) {
            is Document -> DocComponent.Document(children)
            is BlockQuote -> TODO("Not yet implemented")
            is BulletList -> TODO("Not yet implemented")
            is Code -> TODO("Not yet implemented")
            is CustomBlock -> TODO("Not yet implemented")
            is CustomNode -> acceptCustomNode(node, children)
            is Emphasis -> TODO("Not yet implemented")
            is FencedCodeBlock -> TODO("Not yet implemented")
            is HardLineBreak -> DocComponent.HardLineBreak(children)
            is Heading -> DocComponent.Header(node.level, children)
            is HtmlBlock -> TODO("Not yet implemented")
            is HtmlInline -> TODO("Not yet implemented")
            is Image -> TODO("Not yet implemented")
            is IndentedCodeBlock -> TODO("Not yet implemented")
            is Link -> TODO("Not yet implemented")
            is LinkReferenceDefinition -> TODO("Not yet implemented")
            is ListItem -> TODO("Not yet implemented")
            is OrderedList -> TODO("Not yet implemented")
            is Paragraph -> DocComponent.Paragraph(children)
            is SoftLineBreak -> DocComponent.SoftLineBreak(children)
            is StrongEmphasis -> TODO("Not yet implemented")
            is Text -> DocComponent.Literal(node.literal, children)
            is ThematicBreak -> TODO("Not yet implemented")
            else -> error("Unknown node type: ${node.javaClass.simpleName}")
        }
    }

    private fun acceptCustomNode(node: CustomNode, children: List<DocComponent>): DocComponent {
        return when (node) {
            is Placeholder -> DocComponent.Literal(node.identifier, children)
            else -> error("Unknown node type: ${node.javaClass.simpleName}")
        }
    }

    private fun visitChildren(parent: Node): List<DocComponent> {
        return buildList {
            var node = parent.firstChild
            while (node != null) {
                // A subclass of this visitor might modify the node, resulting in getNext returning a different node or no
                // node after visiting it. So get the next node before visiting.
                val next = node.next
                this += accept(node)
                node = next
            }
        }
    }
}