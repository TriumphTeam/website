package dev.triumphteam.website.docs.markdown

import org.commonmark.internal.util.Parsing
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState

public abstract class CustomBlockParserFactory<P : FenceBlockParser> : AbstractBlockParserFactory() {

    public abstract fun checkOpener(line: CharSequence, index: Int, indent: Int): P?

    override fun tryStart(state: ParserState, matchedBlockParser: MatchedBlockParser): BlockStart? {
        val indent = state.indent
        if (indent >= Parsing.CODE_BLOCK_INDENT) {
            return BlockStart.none()
        }

        val nextNonSpace = state.nextNonSpaceIndex
        val blockParser = checkOpener(state.line.content, nextNonSpace, indent) ?: return BlockStart.none()

        return BlockStart.of(blockParser).atIndex(nextNonSpace + blockParser.block.startFenceLength)
    }
}
