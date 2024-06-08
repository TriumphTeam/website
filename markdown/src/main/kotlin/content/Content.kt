package dev.triumphteam.markdown.content

import kotlinx.serialization.Serializable

@Serializable
data class ContentEntry(val literal: String, val href: String, val indent: UInt)

@Serializable
data class ContentData(val link: String, val entries: List<ContentEntry>)