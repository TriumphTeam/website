package dev.triumphteam.markdown

import org.commonmark.internal.util.Parsing
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.ParserState

abstract class FenceBlockParser(private val skipCharacter: Char) : AbstractBlockParser() {

    abstract override fun getBlock(): FenceBlock

    override fun tryContinue(state: ParserState): BlockContinue? {
        val nextNonSpace = state.nextNonSpaceIndex
        var newIndex = state.index
        val line = state.line.content

        if (
            state.indent < Parsing.CODE_BLOCK_INDENT &&
            nextNonSpace < line.length &&
            line[nextNonSpace] == skipCharacter &&
            isClosing(line, nextNonSpace)
        ) {
            // closing fence - we're at end of line, so we can finalize now
            return BlockContinue.finished()
        }

        // skip optional spaces of fence indent
        var i = block.fenceIndent
        val length = line.length
        while (i > 0 && newIndex < length && line[newIndex] == ' ') {
            newIndex++
            i--
        }

        return BlockContinue.atIndex(newIndex)
    }

    private fun isClosing(line: CharSequence, index: Int): Boolean {
        val fences = skip(line, index, line.length) - index
        if (fences < 3) {
            return false
        }

        // spec: The closing code fence [...] may be followed only by spaces, which are ignored.
        val after = Parsing.skipSpaceTab(line, index + fences, line.length)
        return after == line.length
    }

    protected open fun skip(line: CharSequence, startIndex: Int, endIndex: Int): Int {
        for (i in startIndex until endIndex) {
            if (line[i] != skipCharacter) {
                return i
            }
        }

        return endIndex
    }

}