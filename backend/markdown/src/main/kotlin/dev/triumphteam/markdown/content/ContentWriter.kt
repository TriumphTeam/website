package dev.triumphteam.markdown.content

class ContentWriter {

    private val entries = mutableListOf<ContentEntry>()
    private var indent = -1
    private var href = ""

    fun openHeader(indent: Int, href: String) {
        this.indent = indent - 1

        if (this.indent == 0) this.href = href
        else this.href += "-$href"
    }

    fun closeHeader() {
        this.indent = -1
    }

    fun append(literal: String) {
        if (indent == -1) return
        entries.add(ContentEntry(literal, href, indent.toUInt()))
    }

    fun build(): List<ContentEntry> {
        return entries
    }

}