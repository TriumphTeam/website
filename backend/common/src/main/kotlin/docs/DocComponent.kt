package dev.triumphteam.website.docs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public sealed interface DocComponent {

    public val children: List<DocComponent>

    @Serializable
    @SerialName("document")
    public data class Document(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("literal")
    public data class Literal(val content: String, override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("paragraph")
    public data class Paragraph(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("header")
    public data class Header(public val level: Int, override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("soft_line_break")
    public data class SoftLineBreak(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("hard_line_break")
    public data class HardLineBreak(override val children: List<DocComponent>) : DocComponent
}