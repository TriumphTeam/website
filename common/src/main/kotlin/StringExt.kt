package dev.triumphteam.website

public fun String.trimAround(word: String? = null, contextLength: Int = 20): String {
    val index = word?.let { indexOf(it, ignoreCase = true) } ?: 0
    val wordLength = word?.length ?: 0

    // Find start index by moving back contextLength characters and then to the previous space
    var start = (index - contextLength).coerceAtLeast(0)
    while (start > 0 && !this[start - 1].isWhitespace()) {
        start--
    }

    // Find end index by moving forward contextLength characters and then to the next space
    var end = (index + wordLength + contextLength).coerceAtMost(length)
    while (end < length && !this[end].isWhitespace()) {
        end++
    }

    val trimmed = substring(start, end).trim()

    val prefix = if (start > 0) "... " else ""
    val suffix = if (end < length) " ..." else ""

    return "$prefix$trimmed$suffix"
}

public fun String.highlightWord(words: List<String>): String {
    val regex = Regex(words.joinToString("|", transform = Regex.Companion::escape), RegexOption.IGNORE_CASE)
    return regex.replace(this) {
        "<b>${it.value}</b>"
    }
}
