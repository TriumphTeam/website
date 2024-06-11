package dev.triumphteam.website.docs.markdown.content

import dev.triumphteam.website.project.ContentEntry

public class ContentWriter {

    private val entries = mutableListOf<ContentEntry>()
    private var indent = -1
    private var href = ""

    public fun openHeader(indent: Int, href: String) {
        this.indent = indent - 1

        if (this.indent == 0) this.href = href
        else this.href += "-$href"
    }

    public fun closeHeader() {
        this.indent = -1
    }

    public fun append(literal: String) {
        if (indent == -1) return
        entries.add(ContentEntry(literal, href, indent.toUInt()))
    }

    public fun build(): List<ContentEntry> {
        return entries
    }

}
