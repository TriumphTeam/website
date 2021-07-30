package dev.triumphteam.markdown.content

import kotlinx.serialization.Serializable

@Serializable
data class ContentEntry(val literal: String, val indent: UInt)