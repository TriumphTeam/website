package dev.triumphteam.markdown

import org.commonmark.node.CustomBlock

abstract class FenceBlock : CustomBlock() {

    abstract var startFenceLength: Int
    abstract var endFenceLength: Int
    abstract var fenceIndent: Int
}
