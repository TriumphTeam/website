package dev.triumphteam.website.docs.markdown.highlight

internal val NEW_LINE_CHARS = setOf("\n", "\r", "\r\n")

internal fun String.indicesOf(char: Char): Set<Int> {
    return indicesOf(char.toString())
}

internal fun String.indicesOf(string: String): Set<Int> {
    return indicesOf(string.toRegex(RegexOption.LITERAL)).map { it.first }.toSet()
}

internal fun String.indicesOf(regex: Regex): Set<IntRange> {
    return regex.findAll(this).map { it.range }.toSet()
}

internal fun Char.isNewLine(): Boolean {
    return toString() in NEW_LINE_CHARS
}


internal fun String.lengthToEOF(start: Int = 0): Int {
    if (all { it.isNewLine().not() }) return length - start
    var endIndex = start
    while (this.getOrNull(endIndex)?.isNewLine()?.not() == true) {
        endIndex++
    }
    return endIndex - start
}

internal fun Char.escapeHtml(): String {
    return when (this) {
        '&' -> "&amp;"
        '<' -> "&lt;"
        '>' -> "&gt;"
        '\"' -> "&quot;"
        else -> toString()
    }
}
