package dev.triumphteam.website.docs.markdown

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text

public abstract class TextBasedRenderer(
    private val separator: String,
    private val transform: ((String) -> CharSequence)? = null,
) : AbstractVisitor() {

    private val texts = mutableListOf<String>()

    public fun render(node: Node): String {
        node.accept(this)
        return texts.joinToString(separator, transform = transform)
    }

    override fun visit(heading: Heading) {
        visitChildren(heading)
    }

    override fun visit(text: Text) {
        texts.add(text.literal)
    }
}

public class TextRenderer : TextBasedRenderer(separator = " ")

public class HeaderIdRenderer : TextBasedRenderer(
    separator = "-",
    transform = { it.replace(" ", "-").lowercase() }
)

public class TestRenderer : AbstractVisitor() {

    public fun render(node: Node) {
        node.accept(this)
    }
}
