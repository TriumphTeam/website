package dev.triumphteam.markdown.tab

import dev.triumphteam.markdown.CustomBlockParserFactory
import dev.triumphteam.markdown.FenceBlock
import dev.triumphteam.markdown.FenceBlockParser
import org.commonmark.node.Block

private val TAB_REGEX = "\\+(?<tab>.*?)\\+".toRegex()

class TabBlockParser(tabName: String, startFenceLength: Int, indent: Int) : FenceBlockParser('+') {

    private var block = TabBlock(tabName, startFenceLength, fenceIndent = indent)

    override fun getBlock(): FenceBlock {
        return block
    }

    override fun isContainer(): Boolean {
        return true
    }

    override fun canContain(block: Block?): Boolean {
        return block != null && block !is TabBlock
    }

    class Factory : CustomBlockParserFactory<TabBlockParser>() {
        override fun checkOpener(line: CharSequence, index: Int, indent: Int): TabBlockParser? {
            val (tabName) = TAB_REGEX.matchEntire(line)?.destructured ?: return null
            return TabBlockParser(tabName, line.length, indent)
        }
    }

}

data class TabBlock(
    val text: String,
    override var startFenceLength: Int,
    override var endFenceLength: Int = 3,
    override var fenceIndent: Int
) : FenceBlock()