package dev.triumphteam.website.docs.markdown

import dev.triumphteam.website.docs.MARKDOWN_PARSER
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.hint.HintBlock
import dev.triumphteam.website.docs.markdown.placeholder.Placeholder
import dev.triumphteam.website.docs.project.Replacement
import dev.triumphteam.website.docs.project.Value
import dev.triumphteam.website.serializable.BoldComponent
import dev.triumphteam.website.serializable.BulletListComponent
import dev.triumphteam.website.serializable.CodeBlockComponent
import dev.triumphteam.website.serializable.CodeComponent
import dev.triumphteam.website.serializable.Conditional
import dev.triumphteam.website.serializable.ConditionalComponent
import dev.triumphteam.website.serializable.DocComponent
import dev.triumphteam.website.serializable.HardLineBreakComponent
import dev.triumphteam.website.serializable.HeaderComponent
import dev.triumphteam.website.serializable.HintComponent
import dev.triumphteam.website.serializable.HtmlComponent
import dev.triumphteam.website.serializable.ImageComponent
import dev.triumphteam.website.serializable.ItalicComponent
import dev.triumphteam.website.serializable.LinkComponent
import dev.triumphteam.website.serializable.ListItemComponent
import dev.triumphteam.website.serializable.OrderedListComponent
import dev.triumphteam.website.serializable.ParagraphComponent
import dev.triumphteam.website.serializable.QuoteComponent
import dev.triumphteam.website.serializable.RootComponent
import dev.triumphteam.website.serializable.SeparatorComponent
import dev.triumphteam.website.serializable.SoftLineBreakComponent
import dev.triumphteam.website.serializable.StrikethroughComponent
import dev.triumphteam.website.serializable.TextComponent
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

    public fun render(node: Node): RootComponent {
        if (node !is Document) error("Root node must be a Document")
        return renderNode(node) as? RootComponent ?: error("Root node must be a Document")
    }

    private fun renderNode(node: Node): DocComponent {
        val children = renderChildren(node)
        return when (node) {
            is Document -> RootComponent(children)
            is BlockQuote -> QuoteComponent(children)
            is BulletList -> BulletListComponent(children)
            is Code -> CodeComponent(node.literal)
            is CustomBlock -> renderCustomBlock(node, children)
            is CustomNode -> renderCustomNode(node, children)
            is Emphasis -> ItalicComponent(children)

            is FencedCodeBlock -> {
                val languageDefinition = LanguageDefinition.fromString(node.info)
                CodeBlockComponent(
                    lang = node.info,
                    content = languageDefinition.highlightCode(node.literal),
                    raw = node.literal,
                )
            }

            is HardLineBreak -> HardLineBreakComponent

            is Heading -> {
                val headerText = flatten(" ", children)
                HeaderComponent(
                    level = node.level,
                    text = headerText,
                    id = headerText.lowercase().replace(Regex("\\s+"), "-"),
                    children = children,
                )
            }

            is HtmlBlock -> HtmlComponent(node.literal, children)
            is HtmlInline -> HtmlComponent(node.literal, children)

            is Image -> ImageComponent(
                destination = node.destination,
                alt = flatten(" ", children),
                title = node.title,
            )

            is Link -> LinkComponent(node.destination, node.title, children)
            is ListItem -> ListItemComponent(children)
            is OrderedList -> OrderedListComponent(children)
            is Paragraph -> ParagraphComponent(children)
            is SoftLineBreak -> SoftLineBreakComponent
            is StrongEmphasis -> BoldComponent(children)
            is Text -> TextComponent(node.literal)
            is ThematicBreak -> SeparatorComponent
            else -> unsupportedNode(node)
        }
    }

    private fun renderCustomNode(node: CustomNode, children: List<DocComponent>): DocComponent {
        return when (node) {
            is Placeholder -> replace(node.identifier)
            is Strikethrough -> StrikethroughComponent(children)
            else -> unsupportedNode(node)
        }
    }

    private fun renderCustomBlock(node: CustomBlock, children: List<DocComponent>): DocComponent {
        return when (node) {
            is HintBlock -> HintComponent(node.type, children)
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
            return MarkdownRenderer(replacements).render(MARKDOWN_PARSER.parse(markdown.trimIndent()))
        }

        val replacement = requireNotNull(replacements[identifier]) {
            "Could not find replacement for placeholder ${identifier}."
        }

        return when (replacement) {
            is Replacement.Raw -> parseMarkdown(replacement.content)
            is Replacement.Conditional -> ConditionalComponent(
                conditions = replacement.conditions.map { condition ->
                    Conditional(
                        condition = condition.condition,
                        value = when (val conditionalValue = condition.value) {
                            is Value.Raw -> parseMarkdown(conditionalValue.value)
                            is Value.File -> replace(conditionalValue.identifier)
                        },
                    )
                },
            )
        }
    }

    private fun unsupportedNode(node: Node): Nothing = error("Unsupported node type: ${node.javaClass.simpleName}")
}
