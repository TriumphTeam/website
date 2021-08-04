package dev.triumphteam.markdown.summary.writer

import dev.triumphteam.markdown.summary.Entry
import dev.triumphteam.markdown.summary.Header
import dev.triumphteam.markdown.summary.Item
import dev.triumphteam.markdown.summary.UnorderedList

/**
 * This class is quite bad, but it works as intended,
 * I shall rewrite it "tomorrow"
 */
class SummaryWriter {

    private var summary = mutableListOf<Entry>()

    private var header = false
    private var link = false
    private var ul = false
    private var li = false

    private var stringBuilder = StringBuilder()
    private var itemBuilder = ItemBuilder()
    private var listBuilder = ListBuilder()

    fun openHeader() {
        if (ul || li || link) throw InvalidSummaryException("Header is not allowed inside a list!")
        header = true
    }

    fun closeHeader() {
        header = false
        summary.add(Header(stringBuilder.toString()))
        stringBuilder = StringBuilder()
    }

    fun openLi() {
        li = true
    }

    fun closeLi() {
        itemBuilder.build()?.let { listBuilder.append(it) }
        li = false
    }

    fun openLink(destination: String) {
        if (header) throw InvalidSummaryException("Link is not allowed inside a header!")
        if (!li) throw InvalidSummaryException("Link must be inside a list block!")
        itemBuilder = ItemBuilder(destination)
        link = true
    }

    fun closeLink() {
        link = false
    }

    fun openUl() {
        listBuilder = ListBuilder()
    }

    fun closeUl() {
        summary.add(listBuilder.build())
    }

    fun append(value: String) {
        if (link) {
            itemBuilder.append(value)
            return
        }

        stringBuilder.append(value)
    }

    fun build(): List<Entry> {
        return summary
    }

}

private class ItemBuilder(private var destination: String? = null) {

    private val stringBuilder = StringBuilder()

    fun append(string: String): StringBuilder = stringBuilder.append(string)

    fun build() = destination?.let { Item(stringBuilder.toString(), it) }

}

private class ListBuilder() {

    private val list = UnorderedList(mutableListOf())

    fun append(child: Entry) = list.add(child)

    fun build(): UnorderedList = list

}

class InvalidSummaryException(message: String) : RuntimeException(message)