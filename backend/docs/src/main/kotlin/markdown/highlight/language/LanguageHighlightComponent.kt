package dev.triumphteam.website.docs.markdown.highlight.language

import dev.triumphteam.website.docs.markdown.highlight.Highlight

internal const val INVALID_INDEX: Int = -1

public interface LanguageHighlightComponent {

    public fun highlight(code: String): Collection<Highlight>
}


