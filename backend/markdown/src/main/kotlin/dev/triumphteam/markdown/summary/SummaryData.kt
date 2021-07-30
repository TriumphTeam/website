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
@SerialName("LINK")
data class Link(val literal: String, val destination: String, var indent: UInt) : Entry

val Entry.type: UByte
    get() = when (this) {
        is Header -> 0u
        is Link -> 1u
    }

@Serializable
data class SummaryData(val entries: List<Entry>)