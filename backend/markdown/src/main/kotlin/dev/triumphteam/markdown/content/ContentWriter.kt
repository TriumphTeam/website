package dev.triumphteam.markdown.content

class ContentWriter {

    private val entries = mutableListOf<ContentEntry>()
    private var indent = -1

    fun openHeader(indent: Int) {
        this.indent = indent - 1
    }

    fun closeHeader() {
        this.indent = -1
    }

    fun append(literal: String) {
        if (indent == -1) return
        entries.add(ContentEntry(literal, indent.toUInt()))
    }

    fun build(): List<ContentEntry> {
        return entries
    }

}