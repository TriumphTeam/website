package dev.triumphteam.markdown.writer

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
            menuBuilder.build()?.let { summary.add(it) }
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

sealed interface Entry
data class Header(val literal: String) : Entry
data class Link(val literal: String, val destination: String) : Entry
data class Menu(val main: Link, val children: List<Link>) : Entry

class LinkBuilder(private var destination: String? = null) {

    private val stringBuilder = StringBuilder()

    fun append(string: String): StringBuilder = stringBuilder.append(string)

    fun build() = destination?.let { Link(stringBuilder.toString(), it) }

}

class MenuBuilder(private var main: Link? = null) {

    private val children = mutableListOf<Link>()

    fun append(child: Link) = children.add(child)

    fun build(): Menu? = main?.let { Menu(it, children) }

}

class InvalidSummaryException(message: String) : RuntimeException(message)