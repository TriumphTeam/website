package dev.triumphteam.website.docs.markdown.highlight.language.languages

import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.language.CHAR_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.HighlightValidator
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.highlight.language.NUMBER_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.STRING_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.PunctuationHighlighter
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.RegexHighlighter

public object XmlLanguage : LanguageDefinition(
    components = listOf(
        STRING_COMPONENT,
        CHAR_COMPONENT,
        RegexHighlighter(
            type = HighlightType.COMMENT,
            expressions = listOf("<!--.*?-->".toRegex()), // Matches comments
        ),
        RegexHighlighter(
            type = HighlightType.FUNCTION,
            expressions = listOf("(?<=<)[^>]+(?=>)".toRegex()), // Matches content inside the tags
        ),
        PunctuationHighlighter("<>/=?".toList()),
        NUMBER_COMPONENT,
    ),
    globalValidator = listOf(
        // The order matters, string first is easier
        HighlightValidator.InString,
        HighlightValidator.InComment,
    ),
    allowDuplicate = true,
)
