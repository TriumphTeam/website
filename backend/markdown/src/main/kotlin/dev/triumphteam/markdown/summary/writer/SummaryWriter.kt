package dev.triumphteam.markdown.summary.writer

import dev.triumphteam.markdown.summary.Entry
import dev.triumphteam.markdown.summary.Header
import dev.triumphteam.markdown.summary.Link

/**
 * This class is quite bad, but it works as intended,
 * I shall rewrite it "tomorrow"
 */
class SummaryWriter {

    private var summary = mutableListOf<Entry>()

    private var header = false
    private var link = false
    private var ulParent = false
    private var ulChild = false
    private var liParent = false
    private var liChild = false

    private var stringBuilder = StringBuilder()
    private var linkBuilder = LinkBuilder()
    private var menuBuilder = MenuBuilder()

    fun openHeader() {
        if (ulParent || ulChild || link) throw InvalidSummaryException("Header is not allowed inside a list!")
        header = true
    }

    fun closeHeader() {
        header = false
        summary.add(Header(stringBuilder.toString()))
        stringBuilder = StringBuilder()
    }

    fun openLi() {
        if (!ulParent && !ulChild) return

        if (liParent) {
            liChild = true
            return
        }

        liParent = true
    }

    fun closeLi() {
        val link = linkBuilder.build()

        if (liChild) {
            link?.let { menuBuilder.append(it) }
            linkBuilder = LinkBuilder()
            liChild = false
            return
        }

        link?.let { summary.add(it) }
        linkBuilder = LinkBuilder()
        liParent = false
    }

    fun openLink(destination: String) {
        if (header) throw InvalidSummaryException("Link is not allowed inside a header!")
        if (!liParent) throw InvalidSummaryException("Link must be inside a list block!")
        linkBuilder = LinkBuilder(destination)
        link = true
    }

    fun closeLink() {
        link = false
    }

    fun openUl() {
        if (ulParent) {
            if (!liParent) throw InvalidSummaryException("Opening a LI child outside a UL parent is not allowed!")
            ulChild = true
            menuBuilder = MenuBuilder(linkBuilder.build())
        } else ulParent = true
    }

    fun closeUl() {
        if (ulChild) {
            ulChild = false
            summary.addAll(menuBuilder.build())
            menuBuilder = MenuBuilder()
            return
        }

        ulParent = false
    }

    fun append(value: String) {
        if (link) {
            linkBuilder.append(value)
            return
        }

        stringBuilder.append(value)
    }

    fun build(): List<Entry> {
        return summary
    }

}

private class LinkBuilder(private var destination: String? = null) {

    private val stringBuilder = StringBuilder()

    fun append(string: String): StringBuilder = stringBuilder.append(string)

    fun build() = destination?.let { Link(stringBuilder.toString(), it, 0) }

}

private class MenuBuilder(main: Link? = null) {

    private val links = mutableListOf<Link>()

    init {
        main?.let { links.add(it) }
    }

    fun append(child: Link) = links.add(child.apply { indent = 1 })

    fun build(): List<Link> = links

}

class InvalidSummaryException(message: String) : RuntimeException(message)