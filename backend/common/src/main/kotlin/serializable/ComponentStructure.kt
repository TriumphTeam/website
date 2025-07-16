package dev.triumphteam.website.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public interface WithChildren {
    public val children: List<DocComponent>
}

@Serializable
public sealed interface DocComponent {

    @Serializable
    @SerialName("document")
    public data class Root(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("text")
    public data class Text(public val content: String) : DocComponent

    @Serializable
    @SerialName("paragraph")
    public data class Paragraph(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("header")
    public data class Header(
        public val level: Int,
        public val text: String,
        public val id: String,
        override val children: List<DocComponent>,
    ) : DocComponent, WithChildren

    @Serializable
    @SerialName("soft_line_break")
    public data object SoftLineBreak : DocComponent

    @Serializable
    @SerialName("hard_line_break")
    public data object HardLineBreak : DocComponent

    @Serializable
    @SerialName("quote")
    public data class Quote(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("bullet_list")
    public data class BulletList(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("ordered_list")
    public data class OrderedList(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("list_item")
    public data class ListItem(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("code")
    public data class Code(public val content: String) : DocComponent

    @Serializable
    @SerialName("code_block")
    public data class CodeBlock(public val content: String) : DocComponent

    @Serializable
    @SerialName("bold")
    public data class Bold(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("italic")
    public data class Italic(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("strikethrough")
    public data class Strikethrough(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("underline")
    public data class Underline(override val children: List<DocComponent>) : DocComponent, WithChildren

    @Serializable
    @SerialName("Separator")
    public data object Separator : DocComponent

    @Serializable
    @SerialName("Link")
    public data class Link(
        public val destination: String,
        public val title: String?,
        override val children: List<DocComponent>,
    ) : DocComponent, WithChildren

    @Serializable
    @SerialName("Image")
    public data class Image(
        public val destination: String,
        override val children: List<DocComponent>,
    ) : DocComponent, WithChildren

    @Serializable
    @SerialName("html")
    public data class Html(
        public val html: String,
        override val children: List<DocComponent>,
    ) : DocComponent, WithChildren

    @Serializable
    @SerialName("hint")
    public data class Hint(
        public val hintType: HintType,
        override val children: List<DocComponent>,
    ) : DocComponent, WithChildren

    @Serializable
    @SerialName("conditional")
    public data class Conditional(public val condition: Condition, public val value: DocComponent) : DocComponent
}

@Serializable
public sealed interface Condition {

    @Serializable
    @SerialName("language")
    public data class Language(public val language: String) : Condition

    @Serializable
    @SerialName("buildtool")
    public data class BuildTool(public val buildTool: String) : Condition

    @Serializable
    @SerialName("platform")
    public data class Platform(public val platform: String) : Condition
}

@Serializable
public enum class HintType {
    INFO, SUCCESS, WARNING, ERROR;
}
