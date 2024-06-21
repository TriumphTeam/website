package dev.triumphteam.website.docs.markdown.summary

import dev.triumphteam.website.docs.markdown.HeaderIdRenderer
import dev.triumphteam.website.docs.markdown.TextRenderer
import dev.triumphteam.website.project.SummaryEntry
import org.commonmark.node.Heading

/**
 * Creates the summary part of the page.
 * I fucking hate this class.
 */
public class SummaryWriter {

    private val entries = mutableListOf<SummaryEntry>()
    private var builder: EntryBuilder? = null

    public fun append(heading: Heading) {
        if (builder == null) {
            builder = EntryBuilder(
                indent = heading.level,
                href = HeaderIdRenderer().render(heading),
                text = TextRenderer().render(heading)
            )
            return
        }

        if (builder?.indent == heading.level) {
            builder?.build()?.let { entries.add(it) }

            builder = EntryBuilder(
                indent = heading.level,
                href = HeaderIdRenderer().render(heading),
                text = TextRenderer().render(heading)
            )
            return
        }

        builder?.append(heading)
    }

    public fun build(): List<SummaryEntry> {
        builder?.build()?.let { entries.add(it) }
        return entries
    }


    private class EntryBuilder(
        val indent: Int,
        private val href: String,
        private val text: String,
    ) {

        private val entries = mutableListOf<SummaryEntry>()
        private var builder: EntryBuilder? = null

        fun append(heading: Heading) {
            if (builder == null) {
                builder = EntryBuilder(
                    indent = heading.level,
                    href = HeaderIdRenderer().render(heading),
                    text = TextRenderer().render(heading)
                )
                return
            }

            if (builder?.indent == heading.level) {
                builder?.build()?.let { entries.add(it) }

                builder = EntryBuilder(
                    indent = heading.level,
                    href = HeaderIdRenderer().render(heading),
                    text = TextRenderer().render(heading)
                )
                return
            }

            builder?.append(heading)
        }

        fun build(): SummaryEntry {
            builder?.build()?.let { entries.add(it) }
            return SummaryEntry(
                literal = text,
                href = href,
                children = entries,
            )
        }
    }
}
