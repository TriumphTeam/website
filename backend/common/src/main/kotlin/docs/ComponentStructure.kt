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
    @SerialName("text")
    public data class Text(public val content: String, override val children: List<DocComponent>) : DocComponent

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

    @Serializable
    @SerialName("quote")
    public data class Quote(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("bullet_list")
    public data class BulletList(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("ordered_list")
    public data class OrderedList(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("list_item")
    public data class ListItem(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("code")
    public data class Code(public val content: String, override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("code_block")
    public data class CodeBlock(public val content: String, override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("bold")
    public data class Bold(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("italic")
    public data class Italic(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("strikethrough")
    public data class Strikethrough(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("underline")
    public data class Underline(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("Separator")
    public data class Separator(override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("Link")
    public data class Link(
        public val destination: String,
        public val title: String?,
        override val children: List<DocComponent>
    ) : DocComponent

    @Serializable
    @SerialName("Image")
    public data class Image(public val destination: String, override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("html")
    public data class Html(public val html: String, override val children: List<DocComponent>) : DocComponent

    @Serializable
    @SerialName("hint")
    public data class Hint(public val hintType: HintType, override val children: List<DocComponent>) : DocComponent

}

@Serializable
public enum class HintType {
    INFO, SUCCESS, WARNING, ERROR;
}


