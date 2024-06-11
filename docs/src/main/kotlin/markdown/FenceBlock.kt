package dev.triumphteam.website.docs.markdown

import org.commonmark.node.CustomBlock

public abstract class FenceBlock : CustomBlock() {

    public abstract var startFenceLength: Int
    public abstract var endFenceLength: Int
    public abstract var fenceIndent: Int
}
