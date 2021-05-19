package dev.triumphteam.markdown.summary

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Polymorphic
sealed interface Entry

@Serializable
@SerialName("HEADER")
data class Header(val literal: String) : Entry

@Serializable
@SerialName("LINK")
data class Link(val literal: String, val destination: String) : Entry

@Serializable
@SerialName("MENU")
data class Menu(val main: Link, val children: List<Link>) : Entry

val Entry.type: UByte
    get() = when (this) {
        is Header -> 0u
        is Link -> 1u
        is Menu -> 2u
    }

@Serializable
data class Summary(val entries: List<Entry>)