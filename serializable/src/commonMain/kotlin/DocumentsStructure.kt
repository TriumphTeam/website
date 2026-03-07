package dev.triumphteam.website.serializable

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@Serializable
public data class ProjectVersion(
    public val project: String,
    public val name: String,
    public val version: String,
    public val document: VersionDocument,
)

@Serializable
public data class VersionDocument(
    public val versions: List<VersionData>,
    public val color: String,
    public val stable: Boolean,
    public val groups: List<NavigationGroup>,
    public val settings: List<VersionSetting>,
    public val github: String?,
    public val discord: String?,
    public val javadocs: String?,
)

@Serializable
public data class VersionSetting(
    public val id: String,
    public val values: List<String>,
)

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
public data class NavigationGroup(public val name: String, public val pages: List<NavigationPage>)

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
    public val sections: List<PageContent>,
)

@Serializable
public data class PageContent(
    public val id: String,
    public val name: String,
    public val level: Int,
)

@Serializable
public data class FooterNavigation(public val id: String, public val name: String)

public interface WithChildren {
    public val children: List<DocComponent>
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
public data class RootComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(TEXT_COMPONENT_TYPE)
public data class TextComponent(public val content: String) : DocComponent

@Serializable
@SerialName(PARAGRAPH_COMPONENT_TYPE)
public data class ParagraphComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(HEADER_COMPONENT_TYPE)
public data class HeaderComponent(
    public val level: Int,
    public val text: String,
    public val id: String,
    override val children: List<DocComponent>,
) : DocComponent, WithChildren

@Serializable
@SerialName(SOFT_LINE_BREAK_COMPONENT_TYPE)
public data object SoftLineBreakComponent : DocComponent

@Serializable
@SerialName(HARD_LINE_BREAK_COMPONENT_TYPE)
public data object HardLineBreakComponent : DocComponent

@Serializable
@SerialName(QUOTE_COMPONENT_TYPE)
public data class QuoteComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(BULLET_LIST_COMPONENT_TYPE)
public data class BulletListComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(ORDERED_LIST_COMPONENT_TYPE)
public data class OrderedListComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(LIST_ITEM_COMPONENT_TYPE)
public data class ListItemComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

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
public data class BoldComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(ITALIC_COMPONENT_TYPE)
public data class ItalicComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(STRIKETHROUGH_COMPONENT_TYPE)
public data class StrikethroughComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(UNDERLINE_COMPONENT_TYPE)
public data class UnderlineComponent(override val children: List<DocComponent>) : DocComponent, WithChildren

@Serializable
@SerialName(SEPARATOR_COMPONENT_TYPE)
public data object SeparatorComponent : DocComponent

@Serializable
@SerialName(LINK_COMPONENT_TYPE)
public data class LinkComponent(
    public val destination: String,
    public val title: String?,
    override val children: List<DocComponent>,
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
    override val children: List<DocComponent>,
) : DocComponent, WithChildren

@Serializable
@SerialName(HINT_COMPONENT_TYPE)
public data class HintComponent(
    public val hintType: HintType,
    override val children: List<DocComponent>,
) : DocComponent, WithChildren

@Serializable
@SerialName(CONDITIONAL_COMPONENT_TYPE)
public data class ConditionalComponent(public val condition: ComponentCondition, public val value: DocComponent) :
    DocComponent

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
    INFO, SUCCESS, WARNING, ERROR, QUOTE;
}
