package dev.triumphteam.website.docs.project

import dev.triumphteam.website.serializable.Condition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public data class Replacements(private val backing: Map<String, Replacement>) : Map<String, Replacement> by backing

public sealed interface Replacement {

    public data class Raw(public val content: String) : Replacement

    @Serializable
    public data class Conditional(public val conditions: List<ConditionalValue>) : Replacement
}

@Serializable
public data class ConditionalValue(
    public val condition: Condition,
    public val value: Value,
)

@Serializable
public sealed interface Value {

    @Serializable
    @SerialName("file")
    public data class File(public val identifier: String) : Value

    @Serializable
    @SerialName("raw")
    public data class Raw(public val value: String) : Value
}
