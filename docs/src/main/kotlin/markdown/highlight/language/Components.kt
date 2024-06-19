package dev.triumphteam.website.docs.markdown.highlight.language

import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.CommentHighlighter
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.MultilineCommentHighlighter
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.RegexHighlighter
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.StringHighlighter

public val FUNCTION_REGEX: Regex = "(?:`[^\\r\\n`]+`|\\b\\w+)(?=\\s*\\()".toRegex() // Normal calls
public val LAMBDA_FUNCTION_REGEX: Regex = "(?:`[^\\r\\n`]+`|\\w+)(?=\\s*\\{)".toRegex() // Lambda calls
public val GENERIC_FUNCTION_REGEX: Regex = "(?:`[^\\r\\n`]+`|\\w+)(?=\\s*<[\\w.]+>\\s*)".toRegex() // Generic calls
public val GENERIC_LAMBDA_FUNCTION_REGEX: Regex =
    "(?:`[^\\r\\n`]+`|\\w+)(?=\\s*<\\w+>\\s*\\{)".toRegex() // Generics lambda calls

public val SLASH_COMMENT_COMPONENT: LanguageHighlightComponent = CommentHighlighter(setOf("//"))
public val MULTILINE_COMMENT_COMPONENT: LanguageHighlightComponent = MultilineCommentHighlighter()
public val STRING_COMPONENT: LanguageHighlightComponent = StringHighlighter(setOf('"'), '\\')
public val CHAR_COMPONENT: LanguageHighlightComponent = StringHighlighter(setOf('\''), '\\')
public val TYPE_COMPONENT: LanguageHighlightComponent = RegexHighlighter(
    type = HighlightType.TYPE,
    expressions = listOf("\\b[A-Z][a-z]+(?:[A-Z][a-z]+)*\\b".toRegex()), // Any PascalCase words
)
public val ANNOTATION_COMPONENT: LanguageHighlightComponent = RegexHighlighter(
    type = HighlightType.ANNOTATION,
    expressions = listOf("@[A-Z][a-z]+(?:[A-Z][a-z]+)*\\b".toRegex()), // Any PascalCase words that start with an @
)
public val NUMBER_COMPONENT: LanguageHighlightComponent = RegexHighlighter(
    type = HighlightType.NUMBER,
    expressions = listOf("\\b\\d+(?:\\.\\d+)?[lLfFdD]?\\b".toRegex()), // Numbers
)
public val CONSTANT_COMPONENT: LanguageHighlightComponent = RegexHighlighter(
    type = HighlightType.CONSTANT,
    expressions = listOf("\\b[A-Z_]+(?:\\.[A-Z_]+)*\\b".toRegex()), // Constants
)

