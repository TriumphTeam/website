package dev.triumphteam.website.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
public data class ProjectVersion(
    public val project: String,
    public val name: String,
    public val version: Int,
    public val document: VersionDocument,
)

@Serializable
public data class VersionDocument(
    public val versions: Array<VersionData>,
    public val color: String,
    public val stable: Boolean,
    public val groups: Array<NavigationGroup>,
    public val platforms: Array<String>,
    public val languages: Array<String>,
    public val buildTools: Array<String>,
    public val github: String?,
    public val discord: String?,
    public val javadocs: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as VersionDocument

        if (stable != other.stable) return false
        if (!versions.contentEquals(other.versions)) return false
        if (color != other.color) return false
        if (!groups.contentEquals(other.groups)) return false
        if (!platforms.contentEquals(other.platforms)) return false
        if (!languages.contentEquals(other.languages)) return false
        if (!buildTools.contentEquals(other.buildTools)) return false
        if (github != other.github) return false
        if (discord != other.discord) return false
        if (javadocs != other.javadocs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stable.hashCode()
        result = 31 * result + versions.contentHashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + groups.contentHashCode()
        result = 31 * result + platforms.contentHashCode()
        result = 31 * result + languages.contentHashCode()
        result = 31 * result + buildTools.contentHashCode()
        result = 31 * result + (github?.hashCode() ?: 0)
        result = 31 * result + (discord?.hashCode() ?: 0)
        result = 31 * result + (javadocs?.hashCode() ?: 0)
        return result
    }
}

@Serializable
public class ContentSection(
    public val pageId: String,
    public val title: String,
    public val section: String,
    public val sectionId: String,
    public val content: String,
)

@Serializable
public data class VersionData(
    public val reference: String,
    public val current: Boolean,
)

@Serializable
public data class NavigationGroup(public val name: String, public val pages: Array<NavigationPage>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as NavigationGroup

        if (name != other.name) return false
        if (!pages.contentEquals(other.pages)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + pages.contentHashCode()
        return result
    }
}

@Serializable
public data class NavigationPage(public val id: String, public val name: String)

@Serializable
public data class PageDocument(
    public val name: String,
    public val description: String,
    public val banner: String?,
    public val content: RootComponent,
    public val previous: FooterNavigation?,
    public val next: FooterNavigation?,
    public val sections: Array<PageContent>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PageDocument

        if (name != other.name) return false
        if (description != other.description) return false
        if (content != other.content) return false
        if (previous != other.previous) return false
        if (next != other.next) return false
        if (!sections.contentEquals(other.sections)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + (previous?.hashCode() ?: 0)
        result = 31 * result + (next?.hashCode() ?: 0)
        result = 31 * result + sections.contentHashCode()
        return result
    }
}

@Serializable
public data class PageContent(
    public val id: String,
    public val name: String,
    public val level: Int,
)

@Serializable
public data class FooterNavigation(public val id: String, public val name: String)

public interface WithChildren {
    public val children: ComponentChildren
}

public const val ROOT_COMPONENT_TYPE: String = "root"
public const val TEXT_COMPONENT_TYPE: String = "text"
public const val PARAGRAPH_COMPONENT_TYPE: String = "paragraph"
public const val HEADER_COMPONENT_TYPE: String = "header"
public const val SOFT_LINE_BREAK_COMPONENT_TYPE: String = "soft_line_break"
public const val HARD_LINE_BREAK_COMPONENT_TYPE: String = "hard_line_break"
public const val QUOTE_COMPONENT_TYPE: String = "quote"
public const val BULLET_LIST_COMPONENT_TYPE: String = "bullet_list"
public const val ORDERED_LIST_COMPONENT_TYPE: String = "ordered_list"
public const val LIST_ITEM_COMPONENT_TYPE: String = "list_item"
public const val CODE_COMPONENT_TYPE: String = "code"
public const val CODE_BLOCK_COMPONENT_TYPE: String = "code_block"
public const val BOLD_COMPONENT_TYPE: String = "bold"
public const val ITALIC_COMPONENT_TYPE: String = "italic"
public const val STRIKETHROUGH_COMPONENT_TYPE: String = "strikethrough"
public const val UNDERLINE_COMPONENT_TYPE: String = "underline"
public const val SEPARATOR_COMPONENT_TYPE: String = "separator"
public const val LINK_COMPONENT_TYPE: String = "link"
public const val IMAGE_COMPONENT_TYPE: String = "image"
public const val HTML_COMPONENT_TYPE: String = "html"
public const val HINT_COMPONENT_TYPE: String = "hint"
public const val CONDITIONAL_COMPONENT_TYPE: String = "conditional"

@Serializable
@JsName("DocComponent")
public sealed interface DocComponent

@Serializable
@SerialName(ROOT_COMPONENT_TYPE)
public data class RootComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(TEXT_COMPONENT_TYPE)
public data class TextComponent(public val content: String) : DocComponent

@Serializable
@SerialName(PARAGRAPH_COMPONENT_TYPE)
public data class ParagraphComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(HEADER_COMPONENT_TYPE)
public data class HeaderComponent(
    public val level: Int,
    public val text: String,
    public val id: String,
    override val children: ComponentChildren,
) : DocComponent, WithChildren

@Serializable
@SerialName(SOFT_LINE_BREAK_COMPONENT_TYPE)
public data object SoftLineBreakComponent : DocComponent

@Serializable
@SerialName(HARD_LINE_BREAK_COMPONENT_TYPE)
public data object HardLineBreakComponent : DocComponent

@Serializable
@SerialName(QUOTE_COMPONENT_TYPE)
public data class QuoteComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(BULLET_LIST_COMPONENT_TYPE)
public data class BulletListComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(ORDERED_LIST_COMPONENT_TYPE)
public data class OrderedListComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(LIST_ITEM_COMPONENT_TYPE)
public data class ListItemComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(CODE_COMPONENT_TYPE)
public data class CodeComponent(public val content: String) : DocComponent

@Serializable
@SerialName(CODE_BLOCK_COMPONENT_TYPE)
public data class CodeBlockComponent(
    public val lang: String,
    public val content: String,
    public val raw: String,
) : DocComponent

@Serializable
@SerialName(BOLD_COMPONENT_TYPE)
public data class BoldComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(ITALIC_COMPONENT_TYPE)
public data class ItalicComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(STRIKETHROUGH_COMPONENT_TYPE)
public data class StrikethroughComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(UNDERLINE_COMPONENT_TYPE)
public data class UnderlineComponent(override val children: ComponentChildren) : DocComponent, WithChildren

@Serializable
@SerialName(SEPARATOR_COMPONENT_TYPE)
public data object SeparatorComponent : DocComponent

@Serializable
@SerialName(LINK_COMPONENT_TYPE)
public data class LinkComponent(
    public val destination: String,
    public val title: String?,
    override val children: ComponentChildren,
) : DocComponent, WithChildren

@Serializable
@SerialName(IMAGE_COMPONENT_TYPE)
public data class ImageComponent(
    public val destination: String,
    public val alt: String,
    public val title: String?,
) : DocComponent

@Serializable
@SerialName(HTML_COMPONENT_TYPE)
public data class HtmlComponent(
    public val html: String,
    override val children: ComponentChildren,
) : DocComponent, WithChildren

@Serializable
@SerialName(HINT_COMPONENT_TYPE)
public data class HintComponent(
    public val hintType: HintType,
    override val children: ComponentChildren,
) : DocComponent, WithChildren

@Serializable
@SerialName(CONDITIONAL_COMPONENT_TYPE)
public data class ConditionalComponent(public val condition: ComponentCondition, public val value: DocComponent) : DocComponent

public const val LANGUAGE_CONDITION_TYPE: String = "language"
public const val BUILDTOOL_CONDITION_TYPE: String = "buildtool"
public const val PLATFORM_CONDITION_TYPE: String = "platform"

@Serializable
public sealed interface ComponentCondition

@Serializable
@SerialName(LANGUAGE_CONDITION_TYPE)
public data class LanguageCondition(public val language: String) : ComponentCondition

@Serializable
@SerialName(BUILDTOOL_CONDITION_TYPE)
public data class BuildToolCondition(public val buildTool: String) : ComponentCondition

@Serializable
@SerialName(PLATFORM_CONDITION_TYPE)
public data class PlatformCondition(public val platform: String) : ComponentCondition

@Serializable
public enum class HintType {
    INFO, SUCCESS, WARNING, ERROR;
}

@Serializable
public data class ComponentChildren(public val children: Array<DocComponent>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ComponentChildren
        return children.contentEquals(other.children)
    }

    override fun hashCode(): Int {
        return children.contentHashCode()
    }
}
