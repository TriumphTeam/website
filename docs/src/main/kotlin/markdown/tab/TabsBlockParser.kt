package dev.triumphteam.website.docs.markdown.tab

import dev.triumphteam.website.docs.markdown.CustomBlockParserFactory
import dev.triumphteam.website.docs.markdown.FenceBlock
import dev.triumphteam.website.docs.markdown.FenceBlockParser
import org.commonmark.node.Block

public class TabsBlockParser(indent: Int) : FenceBlockParser('-') {

    private var block = TabsBlock(fenceIndent = indent)

    override fun getBlock(): FenceBlock {
        return block
    }

    override fun isContainer(): Boolean {
        return true
    }

    override fun canContain(block: Block?): Boolean {
        return block != null && block !is TabsBlock
    }

    override fun skip(line: CharSequence, startIndex: Int, endIndex: Int): Int {
        for (i in startIndex until endIndex) {
            val char = line[i]
            when (i) {
                0 -> if (char != '-') return i
                1 -> if (char != '+') return i
                2 -> if (char != '-') return i
            }
        }

        return endIndex
    }

    public class Factory : CustomBlockParserFactory<TabsBlockParser>() {
        override fun checkOpener(line: CharSequence, index: Int, indent: Int): TabsBlockParser? {
            val length = line.length

            // No block found
            if (length != 3) return null

            for (i in index until length) {
                val char = line[i]
                when (i) {
                    0 -> if (char != '-') return null
                    1 -> if (char != '+') return null
                    2 -> if (char != '-') return null
                }
            }

            return TabsBlockParser(indent)
        }
    }

}

public data class TabsBlock(
    override var startFenceLength: Int = 3,
    override var endFenceLength: Int = 3,
    override var fenceIndent: Int,
) : FenceBlock()
