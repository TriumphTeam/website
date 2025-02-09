package dev.triumphteam.website.docs.markdown.highlight

public sealed interface HighlightStep {

    public val index: Int
    public val type: HighlightType

    public fun createTag(): String
}

public data class GenericEnd(override val index: Int) : HighlightStep {

    override val type: HighlightType = HighlightType.END

    override fun createTag(): String {
        return type.tag
    }
}

public data class GenericStart(override val index: Int, override val type: HighlightType) : HighlightStep {

    override fun createTag(): String {
        return type.tag
    }
}

public data class Highlight(
    public val start: HighlightStep,
    public val end: HighlightStep,
    public val type: HighlightType,
) {

    public operator fun contains(highlight: Highlight): Boolean {
        return highlight.start.index in start.index + 1..<end.index || highlight.end.index in start.index + 1..<end.index
    }

    public fun steps(): List<HighlightStep> {
        return listOf(start, end)
    }
}

public enum class HighlightType(public val tag: String) {
    STRING("<span class=\"token string\">"),
    COMMENT("<span class=\"token comment\">"),
    PUNCTUATION("<span class=\"token punctuation\">"),
    KEYWORD("<span class=\"token keyword\">"),
    FUNCTION("<span class=\"token function\">"),
    ANNOTATION("<span class=\"token annotation\">"),
    TYPE("<span class=\"token type\">"),
    LABEL("<span class=\"token label\">"),
    NUMBER("<span class=\"token number\">"),
    CONSTANT("<span class=\"token constant\">"),

    END("</span>"),
}
