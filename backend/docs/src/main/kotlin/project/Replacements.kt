package dev.triumphteam.website.docs.project

import dev.triumphteam.website.docs.Condition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public data class Replacements(private val backing: Map<String, Replacement>) : Map<String, Replacement> by backing

public sealed interface Replacement {

    public data class Raw(public val content: String) : Replacement

    @Serializable
    public data class Conditional(
        public val condition: Condition,
        public val value: Value,
    ) : Replacement
}

@Serializable
public sealed interface Value {

    @Serializable
    @SerialName("replacement")
    public data class Replacement(public val identifier: String) : Value

    @Serializable
    @SerialName("raw")
    public data class Raw(public val value: String) : Value
}