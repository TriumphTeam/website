package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.frontend.api.API_BASE_URL
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.FlowTag
import dev.triumphteam.horizon.html.UnsafeApi
import dev.triumphteam.horizon.html.a
import dev.triumphteam.horizon.html.attributes.Target
import dev.triumphteam.horizon.html.br
import dev.triumphteam.horizon.html.code
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.html.h2
import dev.triumphteam.horizon.html.h3
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.img
import dev.triumphteam.horizon.html.li
import dev.triumphteam.horizon.html.ol
import dev.triumphteam.horizon.html.pre
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.html.ul
import dev.triumphteam.horizon.html.video
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.website.serializable.BoldComponent
import dev.triumphteam.website.serializable.BulletListComponent
import dev.triumphteam.website.serializable.CodeBlockComponent
import dev.triumphteam.website.serializable.CodeComponent
import dev.triumphteam.website.serializable.ConditionalComponent
import dev.triumphteam.website.serializable.DocComponent
import dev.triumphteam.website.serializable.HardLineBreakComponent
import dev.triumphteam.website.serializable.HeaderComponent
import dev.triumphteam.website.serializable.HintComponent
import dev.triumphteam.website.serializable.HintType
import dev.triumphteam.website.serializable.HtmlComponent
import dev.triumphteam.website.serializable.ImageComponent
import dev.triumphteam.website.serializable.ItalicComponent
import dev.triumphteam.website.serializable.LinkComponent
import dev.triumphteam.website.serializable.ListItemComponent
import dev.triumphteam.website.serializable.OrderedListComponent
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.ParagraphComponent
import dev.triumphteam.website.serializable.QuoteComponent
import dev.triumphteam.website.serializable.RootComponent
import dev.triumphteam.website.serializable.SeparatorComponent
import dev.triumphteam.website.serializable.SettingValue
import dev.triumphteam.website.serializable.SoftLineBreakComponent
import dev.triumphteam.website.serializable.StrikethroughComponent
import dev.triumphteam.website.serializable.TextComponent
import dev.triumphteam.website.serializable.UnderlineComponent
import kotlinx.browser.window

public fun FlowContent.docsComponents(
    document: PageDocument,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    rootComponent(document.content, settingStates)
}

private fun FlowContent.childComponent(
    children: List<DocComponent>,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    fun simpleText(content: String) {
        if (this is FlowTag) {
            text(content)
            return
        }

        span { text(content) }
    }

    children.forEach { component ->
        when (component) {
            is RootComponent -> rootComponent(component, settingStates)
            is HtmlComponent -> htmlComponent(component)
            is HeaderComponent -> headerComponent(component, settingStates)
            is TextComponent -> simpleText(component.content)
            is ParagraphComponent -> paragraphComponent(component, settingStates)
            is SoftLineBreakComponent -> simpleText(" ") // Soft line break is just a space.
            is HardLineBreakComponent -> br()
            is QuoteComponent -> quoteComponent(component, settingStates)
            is BulletListComponent -> bulletListComponent(component, settingStates)
            is OrderedListComponent -> orderListComponent(component, settingStates)
            is CodeComponent -> codeComponent(component)
            is CodeBlockComponent -> codeBlockComponent(component)

            is BoldComponent -> {
                span(className = "font-bold") {
                    childComponent(component.children, settingStates)
                }
            }

            is ItalicComponent -> {
                span(className = "italic") {
                    childComponent(component.children, settingStates)
                }
            }

            is StrikethroughComponent -> {
                span(className = "line-through") {
                    childComponent(component.children, settingStates)
                }
            }

            is UnderlineComponent -> {
                span(className = "underline") {
                    childComponent(component.children, settingStates)
                }
            }

            is SeparatorComponent -> separator()
            is LinkComponent -> linkComponent(component, settingStates)
            is ImageComponent -> imageComponent(component)
            is HintComponent -> hintComponent(component, settingStates)
            is ConditionalComponent -> conditionalComponent(component, settingStates)

            else -> {}
        }
    }
}

private fun FlowContent.rootComponent(
    component: RootComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    childComponent(component.children, settingStates)
}

private fun FlowContent.htmlComponent(component: HtmlComponent) {
    div(className = "p-2") {
        // TODO: Needs parsing.
    }
}

private fun FlowContent.headerComponent(
    component: HeaderComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    val textSize = when (component.level) {
        1 -> "text-2xl"
        2 -> "text-xl"
        3 -> "text-lg"
        else -> "text-base"
    }

    div(id = "doc-section", className = "group py-4") {
        a(
            href = "#${component.id}",
            className = "$textSize inline-flex font-medium text-dark-text-primary",
        ) {
            span(id = "hash", className = "absolute -ml-6 opacity-0 group-hover:opacity-20 transition-opacity") {
                text("#")
            }
            when (component.level) {
                1 -> h1(id = component.id, className = textSize) {
                    childComponent(component.children, settingStates)
                }

                2 -> h2(id = component.id, className = textSize) {
                    childComponent(component.children, settingStates)
                }

                else -> h3(id = component.id, className = textSize) {
                    childComponent(component.children, settingStates)
                }
            }
        }
    }
}

private fun FlowContent.paragraphComponent(
    component: ParagraphComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    div(className = "leading-8") { childComponent(component.children, settingStates) }
}

private fun FlowContent.quoteComponent(
    component: QuoteComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    hintBlock(HintType.QUOTE, component.children, settingStates)
}

private fun FlowContent.hintBlock(
    type: HintType,
    children: List<DocComponent>,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    val color = when (type) {
        HintType.INFO -> "border-(--hint-info)"
        HintType.SUCCESS -> "border-(--hint-success)"
        HintType.WARNING -> "border-(--hint-warning)"
        HintType.ERROR -> "border-(--hint-error)"
        HintType.QUOTE -> "border-neutral-400"
    }

    val icon = when (type) {
        HintType.INFO -> "bx bx-message-circle-exclamation text-(--hint-info)"
        HintType.SUCCESS -> "bx bx-message-circle-check text-(--hint-success)"
        HintType.WARNING -> "bx bx-message-circle-exclamation text-(--hint-warning)"
        HintType.ERROR -> "bx bx-message-circle-x text-(--hint-error)"
        HintType.QUOTE -> null
    }

    div(className = "border-l-4 $color bg-dark-surface-transparent !pl-4 !py-4 my-2 mx-2 rounded-l-sm rounded-r-md flex flex-row items-center") {
        if (icon != null) {
            i(className = "$icon pr-4 text-xl")
        }
        childComponent(children, settingStates)
    }
}

private fun FlowContent.bulletListComponent(
    component: BulletListComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    ul(className = "list-disc list-outside !pl-8 [&_*]:leading-3") {
        listItemComponent(component.children, settingStates)
    }
}

public fun FlowContent.orderListComponent(
    component: OrderedListComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    ol(className = "list-decimal list-outside !pl-8 [&_*]:leading-3") {
        listItemComponent(component.children, settingStates)
    }
}

private fun FlowTag.listItemComponent(
    components: List<DocComponent>,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    components.forEach { component ->
        when (component) {
            is ListItemComponent -> {
                li(className = "py-2") {
                    childComponent(component.children, settingStates)
                }
            }

            is BulletListComponent -> bulletListComponent(component, settingStates)
            is OrderedListComponent -> orderListComponent(component, settingStates)

            else -> {}
        }
    }
}

private fun FlowContent.codeComponent(component: CodeComponent) {
    code { text(component.content) }
}

@OptIn(UnsafeApi::class)
private fun FlowContent.codeBlockComponent(component: CodeBlockComponent) {
    val icon = "bx bx-copy cursor-pointer"
    val className =
        "$icon text-xl absolute right-0 -translate-x-[125%] translate-y-full opacity-0 group-hover:opacity-20 transition-opacity"

    div(className = "relative group py-2") {
        i(className = className) {
            onClick = {
                window.navigator.clipboard.writeText(component.raw)
                    .then(
                        onFulfilled = {
                            // TODO: Uh.
                        },
                        onRejected = {},
                    )
            }
        }
        pre(lang = component.lang) {
            code(lang = component.lang) {
                // TODO: Parse component.content
                innerHtml(component.content)
            }
        }
    }
}

private fun FlowContent.linkComponent(
    component: LinkComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    val className = "text-(--project-color) hover:text-(--project-color)/70 transition-colors duration-300 ease-in-out"

    if (!component.destination.startsWith("/")) {
        a(
            className = className,
            href = component.destination,
            target = Target.BLANK,
            rel = "noreferrer",
        ) {
            childComponent(component.children, settingStates)
        }
        return
    }

    navigate(
        to = "..${component.destination}",
        className = className,
    ) {
        childComponent(component.children, settingStates)
    }
}

private fun FlowContent.imageComponent(component: ImageComponent) {
    val destination = when {
        component.destination.startsWith("/") -> "$API_BASE_URL/assets${component.destination}"
        else -> component.destination
    }

    if (destination.endsWith(".mp4")) {
        video(
            src = destination,
            loop = true,
            autoPlay = true,
            muted = true,
            className = "w-full md:max-w-[620px] rounded-lg",
        )
        return
    }

    img(
        src = destination,
        alt = component.alt,
        title = component.title,
        className = "w-full md:max-w-[620px] rounded-lg",
    )
}

private fun FlowContent.hintComponent(
    component: HintComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    hintBlock(component.hintType, component.children, settingStates)
}

private fun FlowContent.conditionalComponent(
    component: ConditionalComponent,
    settingStates: Map<String, MutableState<SettingValue>>,
) {
    component.conditions.forEach { condition ->
        val settingState = settingStates[condition.condition.id] ?: return@forEach

        component {
            val state by remember(settingState)

            render {
                if (state.id != condition.condition.value) return@render
                childComponent(listOf(condition.value), settingStates)
            }
        }
    }
}

