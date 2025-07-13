package dev.triumphteam.website.docs.project

import jdk.javadoc.internal.doclets.toolkit.taglets.snippet.Replace
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public data class Replacements(private val backing: Map<String, Replacement>) : Map<String, Replacement> by backing

public sealed interface Replacement {

    public data class Markdown(public val content: String) : Replacement

    @Serializable
    public data class Hocon(public val conditions: List<Conditional>) : Replacement
}

@Serializable
public data class Conditional(
    public val condition: Condition,
    public val value: Value,
)

@Serializable
public sealed interface Condition {
    @Serializable
    @SerialName("language")
    public data class Language(public val language: String) : Condition

    @Serializable
    @SerialName("buildtool")
    public data class BuildTool(public val buildTool: String) : Condition
}

@Serializable
public sealed interface Value {

    @Serializable
    @SerialName("file")
    public data class File(public val path: String) : Value

    @Serializable
    @SerialName("raw")
    public data class Raw(public val value: String) : Value
}