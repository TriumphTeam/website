package dev.triumphteam.website.docs.project

public data class Replacements(private val backing: Map<String, Replacement>) : Map<String, Replacement> by backing

public sealed interface Replacement {

    public data class Markdown(val content: String) : Replacement
}