package dev.triumphteam.frontend.internal

import org.w3c.dom.DOMRect
import org.w3c.dom.Element

public external interface IntersectionObserverEntry {
    public val isIntersecting: Boolean
    public val target: Element
    public val intersectionRatio: Double
    public val boundingClientRect: DOMRect
    public val intersectionRect: DOMRect
    public val rootBounds: DOMRect?
    public val time: Double
}

public external interface IntersectionObserverInit {
    public var root: Element?
    public var rootMargin: String?
    public var threshold: Any?
}

public external class IntersectionObserver(
    callback: (entries: Array<IntersectionObserverEntry>, observer: IntersectionObserver) -> Unit,
    options: IntersectionObserverInit = definedExternally,
) {
    public fun observe(target: Element)
    public fun unobserve(target: Element)
    public fun disconnect()
    public fun takeRecords(): Array<IntersectionObserverEntry>
}
