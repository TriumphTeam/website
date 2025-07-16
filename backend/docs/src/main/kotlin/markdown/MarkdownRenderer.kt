package dev.triumphteam.website.docs.markdown

import dev.triumphteam.website.serializable.DocComponent
import dev.triumphteam.website.docs.MARKDOWN_PARSER
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.hint.HintBlock
import dev.triumphteam.website.docs.markdown.placeholder.Placeholder
import dev.triumphteam.website.docs.project.Replacement
import dev.triumphteam.website.docs.project.Value
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

    public fun render(node: Node): DocComponent.Root {
        if (node !is Document) error("Root node must be a Document")
        return renderNode(node) as? DocComponent.Root ?: error("Root node must be a Document")
    }

    private fun renderNode(node: Node): DocComponent {
        val children = renderChildren(node)
        return when (node) {
            is Document -> DocComponent.Root(children)
            is BlockQuote -> DocComponent.Quote(children)
            is BulletList -> DocComponent.BulletList(children)
            is Code -> DocComponent.Code(node.literal)
            is CustomBlock -> renderCustomBlock(node, children)
            is CustomNode -> renderCustomNode(node, children)
            is Emphasis -> DocComponent.Italic(children)

            is FencedCodeBlock -> {
                val languageDefinition = LanguageDefinition.fromString(node.info)
                DocComponent.CodeBlock(content = languageDefinition.highlightCode(node.literal))
            }

            is HardLineBreak -> DocComponent.HardLineBreak

            is Heading -> {
                val headerText = flatten(" ", children)
                DocComponent.Header(
                    level = node.level,
                    text = headerText,
                    id = headerText.lowercase().replace(Regex("\\s+"), "-"),
                    children = children
                )
            }

            is HtmlBlock -> DocComponent.Html(node.literal, children)
            is HtmlInline -> DocComponent.Html(node.literal, children)
            is Image -> DocComponent.Image(node.destination, children)
            is Link -> DocComponent.Link(node.destination, node.title, children)
            is ListItem -> DocComponent.ListItem(children)
            is OrderedList -> DocComponent.OrderedList(children)
            is Paragraph -> DocComponent.Paragraph(children)
            is SoftLineBreak -> DocComponent.SoftLineBreak
            is StrongEmphasis -> DocComponent.Bold(children)
            is Text -> DocComponent.Text(node.literal)
            is ThematicBreak -> DocComponent.Separator
            else -> unsupportedNode(node)
        }
    }

    private fun renderCustomNode(node: CustomNode, children: List<DocComponent>): DocComponent {
        return when (node) {
            is Placeholder -> replace(node.identifier)
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

    private fun replace(identifier: String): DocComponent {

        fun parseMarkdown(markdown: String): DocComponent {
            return MarkdownRenderer(replacements).render(MARKDOWN_PARSER.parse(markdown))
        }

        val replacement = requireNotNull(replacements[identifier]) {
            "Could not find replacement for placeholder ${identifier}."
        }

        return when (replacement) {
            is Replacement.Raw -> parseMarkdown(replacement.content)
            is Replacement.Conditional -> DocComponent.Conditional(
                condition = replacement.condition,
                value = when (val replacementValue = replacement.value) {
                    is Value.Raw -> parseMarkdown(replacementValue.value)
                    is Value.Replacement -> replace(replacementValue.identifier)
                }
            )
        }
    }

    private fun unsupportedNode(node: Node): Nothing = error("Unsupported node type: ${node.javaClass.simpleName}")
}