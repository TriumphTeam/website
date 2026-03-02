package dev.triumphteam.frontend.internal

import org.w3c.dom.Element
import kotlin.js.Date

public val now: Date = Date()

public fun Element.showModal() {
    asDynamic().showModal()
}
