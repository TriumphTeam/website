@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package dev.triumphteam.frontend.internal

@JsModule("fuse.js")
@JsNonModule
public external class Fuse<T>(list: Array<T>, options: FuseOptions = definedExternally) {
   public fun search(pattern: String): Array<FuseResult<T>>
   public fun setCollection(list: Array<T>)
}

public external interface FuseOptions {
    public var keys: Array<String>?
    public var threshold: Double?
    public var includeScore: Boolean?
    public var includeMatches: Boolean?
    public var minMatchCharLength: Int?
    public var ignoreLocation: Boolean?
    public var distance: Int?
    public var findAllMatches: Boolean?
    public var useExtendedSearch: Boolean?
}

public external interface FuseResult<T> {
    public val item: T
    public val score: Double?
    public val matches: Array<FuseMatch>?
}

public external interface FuseMatch {
   public val indices: Array<Array<Int>>
   public val key: String
   public val value: String
}

// Helper function to create options
public fun fuseOptions(block: FuseOptions.() -> Unit): FuseOptions {
    return (js("{}") as FuseOptions).apply(block)
}
