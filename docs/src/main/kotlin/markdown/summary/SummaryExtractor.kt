package dev.triumphteam.website.docs.markdown.summary

import dev.triumphteam.website.docs.markdown.HeaderIdRenderer
import dev.triumphteam.website.docs.markdown.TextRenderer
import dev.triumphteam.website.project.Page
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text

public class SummaryExtractor : AbstractVisitor() {

    private var currentLevel = 0
    private val mainBuilder = SummaryEntryBuilder(-1)

    public fun extract(node: Node): List<Page.Summary> {
        node.accept(this)
        return finalize()
    }

    override fun visit(heading: Heading) {
        currentLevel = heading.level
        mainBuilder.append(heading)
    }

    override fun visit(text: Text) {
        mainBuilder.appendSearch(currentLevel, text.literal)
    }

    private fun finalize(): List<Page.Summary> {
        return mainBuilder.build().children
    }
}

private data class SummaryEntryBuilder(
    val level: Int,
    private val href: String = "",
    private val text: String = "",
) {

    private val entries = mutableListOf<Page.Summary>()
    private val searches = mutableListOf<String>()
    private var builder: SummaryEntryBuilder? = null

    fun append(heading: Heading) {
        if (builder == null) {
            builder = SummaryEntryBuilder(
                level = heading.level,
                href = HeaderIdRenderer().render(heading),
                text = TextRenderer().render(heading)
            )
            return
        }

        if (builder?.level == heading.level) {
            builder?.build()?.let { entries.add(it) }

            builder = SummaryEntryBuilder(
                level = heading.level,
                href = HeaderIdRenderer().render(heading),
                text = TextRenderer().render(heading)
            )
            return
        }

        builder?.append(heading)
    }

    fun appendSearch(level: Int, search: String) {
        if (level == this.level) {
            searches.add(search)
            return
        }

        builder?.appendSearch(level, search)
    }

    fun build(): Page.Summary {
        builder?.build()?.let { entries.add(it) }
        return Page.Summary(
            literal = text,
            href = href,
            terms = searches.toList(),
            children = entries.toList(),
        )
    }
}
