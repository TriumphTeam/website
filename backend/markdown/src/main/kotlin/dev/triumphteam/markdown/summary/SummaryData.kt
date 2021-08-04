package dev.triumphteam.markdown.summary

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Main sealed interface for the entries
 */
@Polymorphic
sealed interface Entry

@Serializable
@SerialName("HEADER")
data class Header(val literal: String) : Entry

@Serializable
@SerialName("ITEM")
data class Item(val literal: String, val destination: String) : Entry

@Serializable
@SerialName("LIST")
data class UnorderedList(val children: MutableList<Entry>) : Entry {

    fun add(entry: Entry) {
        children.add(entry)
    }

}