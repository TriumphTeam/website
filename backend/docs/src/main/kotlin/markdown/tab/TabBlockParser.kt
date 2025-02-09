package dev.triumphteam.website.docs.markdown.tab

import dev.triumphteam.website.docs.markdown.CustomBlockParserFactory
import dev.triumphteam.website.docs.markdown.FenceBlock
import dev.triumphteam.website.docs.markdown.FenceBlockParser
import org.commonmark.node.Block

private val TAB_REGEX = "\\+(?<tab>.*?)\\+".toRegex()

public class TabBlockParser(tabName: String, startFenceLength: Int, indent: Int) : FenceBlockParser('+') {

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

    public class Factory : CustomBlockParserFactory<TabBlockParser>() {
        override fun checkOpener(line: CharSequence, index: Int, indent: Int): TabBlockParser? {
            val (tabName) = TAB_REGEX.matchEntire(line)?.destructured ?: return null
            return TabBlockParser(tabName, line.length, indent)
        }
    }

}

public data class TabBlock(
    val text: String,
    override var startFenceLength: Int,
    override var endFenceLength: Int = 3,
    override var fenceIndent: Int
) : FenceBlock()
