package dev.triumphteam.website.docs.markdown.highlight

/*
import dev.snipme.highlights.internal.locator.AnnotationLocator
import dev.snipme.highlights.internal.locator.CommentLocator
import dev.snipme.highlights.internal.locator.KeywordLocator
import dev.snipme.highlights.internal.locator.MarkLocator
import dev.snipme.highlights.internal.locator.MultilineCommentLocator
import dev.snipme.highlights.internal.locator.NumericLiteralLocator
import dev.snipme.highlights.internal.locator.PunctuationLocator
import dev.snipme.highlights.internal.locator.StringLocator
import dev.snipme.highlights.internal.toRangeSet
import dev.snipme.highlights.model.CodeStructure

private fun analyzeCodeWithKeywords(code: String, keywords: Set<String>): CodeStructure {
    val comments = CommentLocator.locate(code)
    val multiLineComments = MultilineCommentLocator.locate(code)
    val commentRanges = (comments + multiLineComments).toRangeSet()

    val strings = StringLocator.locate(code, commentRanges)
    val plainTextRanges = (comments + multiLineComments + strings).toRangeSet()

    // TODO Apply ignored ranges to other locators
    return CodeStructure(
        marks = MarkLocator.locate(code),
        punctuations = PunctuationLocator.locate(code),
        keywords = KeywordLocator.locate(code, keywords, plainTextRanges),
        strings = strings,
        literals = NumericLiteralLocator.locate(code),
        comments = comments,
        multilineComments = multiLineComments,
        annotations = AnnotationLocator.locate(code),
        incremental = false,
    )
}
*/
