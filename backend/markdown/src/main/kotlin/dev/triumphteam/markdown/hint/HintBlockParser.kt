package dev.triumphteam.markdown.hint

import org.commonmark.internal.util.Parsing
import org.commonmark.node.Block
import org.commonmark.node.CustomBlock
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

private const val HINT_CHAR = '!'

class HintBlockParser(
    type: HintType,
    fenceLength: Int,
    indent: Int
) : AbstractBlockParser() {

    private var block = HintBlock(type, fenceLength, indent)

    override fun getBlock(): Block {
        return block
    }

    override fun isContainer(): Boolean {
        return true
    }

    override fun canContain(block: Block?): Boolean {
        return block != null && block !is HintBlock
    }

    override fun tryContinue(state: ParserState): BlockContinue? {
        val nextNonSpace = state.nextNonSpaceIndex
        var newIndex = state.index
        val line = state.line.content
        if (
            state.indent < Parsing.CODE_BLOCK_INDENT &&
            nextNonSpace < line.length &&
            line[nextNonSpace] == HINT_CHAR &&
            isClosing(line, nextNonSpace)
        ) {
            // closing fence - we're at end of line, so we can finalize now
            return BlockContinue.finished()
        }

        // skip optional spaces of fence indent
        var i: Int = block.fenceIndent
        val length = line.length
        while (i > 0 && newIndex < length && line[newIndex] == ' ') {
            newIndex++
            i--
        }

        return BlockContinue.atIndex(newIndex)
    }

    private fun isClosing(line: CharSequence, index: Int): Boolean {
        val fences = Parsing.skip(HINT_CHAR, line, index, line.length) - index
        if (fences < 3) {
            return false
        }
        // spec: The closing code fence [...] may be followed only by spaces, which are ignored.
        val after = Parsing.skipSpaceTab(line, index + fences, line.length)
        return after == line.length
    }

    class Factory : AbstractBlockParserFactory() {
        override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart? {
            val indent = state.indent
            if (indent >= Parsing.CODE_BLOCK_INDENT) {
                return BlockStart.none()
            }

            val nextNonSpace = state.nextNonSpaceIndex
            val blockParser = checkOpener(state.line.content, nextNonSpace, indent) ?: return BlockStart.none()

            return BlockStart.of(blockParser).atIndex(nextNonSpace + blockParser.block.fenceLength)
        }
    }

    data class HintBlock(val type: HintType, var fenceLength: Int, var fenceIndent: Int) : CustomBlock()

    enum class HintType(val icon: String) {
        INFO(INFO_ICON),
        SUCCESS(SUCCESS_ICON),
        WARNING(WARNING_ICON),
        ERROR(ERROR_ICON);

        companion object {
            fun fromChar(token: Char?): HintType? {
                if (token == null || token == ' ') return INFO

                return when (token) {
                    'v' -> SUCCESS
                    'x' -> ERROR
                    '!' -> WARNING
                    else -> null
                }
            }
        }
    }

}

private fun checkOpener(line: CharSequence, index: Int, indent: Int): HintBlockParser? {
    var fenceLength = 0
    val length = line.length

    // No exclamations and no type
    if (length < 3) return null

    for (i in index until length) {
        if (line[i] != HINT_CHAR) break
        if (fenceLength >= 3) break
        fenceLength++
    }

    val typeChar = line.getOrNull(3)

    if (fenceLength < 3) return null

    if (typeChar != null) fenceLength++
    val type = HintBlockParser.HintType.fromChar(line.getOrNull(3)) ?: return null

    return HintBlockParser(type, fenceLength, indent)
}

private const val INFO_ICON = """
<svg class="hint-svg-icon" focusable="false" viewBox="0 0 24 24" aria-hidden="true">
  <path d="M11,9H13V7H11M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20, 12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10, 10 0 0,0 12,2M11,17H13V11H11V17Z"></path>
</svg>
"""

private const val SUCCESS_ICON = """
<svg class="hint-svg-icon" focusable="false" viewBox="0 0 24 24" aria-hidden="true">
  <path d="M20,12A8,8 0 0,1 12,20A8,8 0 0,1 4,12A8,8 0 0,1 12,4C12.76,4 13.5,4.11 14.2, 4.31L15.77,2.74C14.61,2.26 13.34,2 12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0, 0 22,12M7.91,10.08L6.5,11.5L11,16L21,6L19.59,4.58L11,13.17L7.91,10.08Z"></path>
</svg>
"""

private const val WARNING_ICON = """
<svg class="hint-svg-icon" focusable="false" viewBox="0 0 24 24" aria-hidden="true">
  <path d="M12 5.99L19.53 19H4.47L12 5.99M12 2L1 21h22L12 2zm1 14h-2v2h2v-2zm0-6h-2v4h2v-4z"></path>
</svg>
"""

private const val ERROR_ICON = """
<svg class="hint-svg-icon" focusable="false" viewBox="0 0 24 24" aria-hidden="true">
  <path d="M11 15h2v2h-2zm0-8h2v6h-2zm.99-5C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"></path>
</svg>
"""