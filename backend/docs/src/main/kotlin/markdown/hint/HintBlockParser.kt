package dev.triumphteam.website.docs.markdown.hint

import dev.triumphteam.website.docs.HintType
import dev.triumphteam.website.docs.markdown.fence.CustomBlockParserFactory
import dev.triumphteam.website.docs.markdown.fence.FenceBlock
import dev.triumphteam.website.docs.markdown.fence.FenceBlockParser
import org.commonmark.node.Block

private const val HINT_CHAR = '!'

public class HintBlockParser(
    type: HintType,
    startFenceLength: Int,
    indent: Int,
) : FenceBlockParser(HINT_CHAR) {

    private var block = HintBlock(type, startFenceLength, fenceIndent = indent)

    override fun getBlock(): FenceBlock {
        return block
    }

    override fun isContainer(): Boolean {
        return true
    }

    override fun canContain(block: Block?): Boolean {
        return block != null && block !is HintBlock
    }

    public class Factory : CustomBlockParserFactory<HintBlockParser>() {
        override fun checkOpener(line: CharSequence, index: Int, indent: Int): HintBlockParser? {
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
            val type = HintType.fromChar(line.getOrNull(3)) ?: return null

            return HintBlockParser(type, fenceLength, indent)
        }
    }
}

public fun HintType.Companion.fromChar(token: Char?): HintType? {
    if (token == null || token == ' ') return HintType.INFO

    return when (token) {
        'v' -> HintType.SUCCESS
        'x' -> HintType.ERROR
        '!' -> HintType.WARNING
        else -> null
    }
}

public data class HintBlock(
    val type: HintType,
    override var startFenceLength: Int,
    override var endFenceLength: Int = 3,
    override var fenceIndent: Int,
) : FenceBlock()
