package dev.triumphteam.frontend.pages.docs.components

import dev.triumphteam.frontend.api.API_BASE_URL
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
import dev.triumphteam.website.serializable.BoldComponent
import dev.triumphteam.website.serializable.BuildToolCondition
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
import dev.triumphteam.website.serializable.LanguageCondition
import dev.triumphteam.website.serializable.LinkComponent
import dev.triumphteam.website.serializable.ListItemComponent
import dev.triumphteam.website.serializable.OrderedListComponent
import dev.triumphteam.website.serializable.PageDocument
import dev.triumphteam.website.serializable.ParagraphComponent
import dev.triumphteam.website.serializable.PlatformCondition
import dev.triumphteam.website.serializable.QuoteComponent
import dev.triumphteam.website.serializable.RootComponent
import dev.triumphteam.website.serializable.SeparatorComponent
import dev.triumphteam.website.serializable.SoftLineBreakComponent
import dev.triumphteam.website.serializable.StrikethroughComponent
import dev.triumphteam.website.serializable.TextComponent
import dev.triumphteam.website.serializable.UnderlineComponent
import kotlinx.browser.window

public fun FlowTag.docsComponents(document: PageDocument) {
    rootComponent(document.content)
}

private fun FlowTag.childComponent(children: List<DocComponent>) {
    children.forEach { component ->
        when (component) {
            is RootComponent -> rootComponent(component)
            is HtmlComponent -> htmlComponent(component)
            is HeaderComponent -> headerComponent(component)
            is TextComponent -> text(component.content)
            is ParagraphComponent -> paragraphComponent(component)
            is SoftLineBreakComponent -> text(" ") // Soft line break is just a space.
            is HardLineBreakComponent -> br()
            is QuoteComponent -> quoteComponent(component)
            is BulletListComponent -> bulletListComponent(component)
            is OrderedListComponent -> orderListComponent(component)
            is CodeComponent -> codeComponent(component)
            is CodeBlockComponent -> codeBlockComponent(component)

            is BoldComponent -> {
                span(className = "font-bold") {
                    childComponent(component.children)
                }
            }

            is ItalicComponent -> {
                span(className = "italic") {
                    childComponent(component.children)
                }
            }

            is StrikethroughComponent -> {
                span(className = "line-through") {
                    childComponent(component.children)
                }
            }

            is UnderlineComponent -> {
                span(className = "underline") {
                    childComponent(component.children)
                }
            }

            is SeparatorComponent -> separator()
            is LinkComponent -> linkComponent(component)
            is ImageComponent -> imageComponent(component)
            is HintComponent -> hintComponent(component)
            is ConditionalComponent -> conditionalComponent(component)

            else -> {}
        }
    }
}

private fun FlowTag.rootComponent(component: RootComponent) {
    childComponent(component.children)
}

private fun FlowContent.htmlComponent(component: HtmlComponent) {
    div(className = "p-2") {
        // TODO: Needs parsing.
    }
}

private fun FlowContent.headerComponent(component: HeaderComponent) {
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
                    childComponent(component.children)
                }

                2 -> h2(id = component.id, className = textSize) {
                    childComponent(component.children)
                }

                else -> h3(id = component.id, className = textSize) {
                    childComponent(component.children)
                }
            }
        }
    }
}

private fun FlowContent.paragraphComponent(component: ParagraphComponent) {
    div(className = "leading-8") { childComponent(component.children) }
}

private fun FlowContent.quoteComponent(component: QuoteComponent) {
    hintBlock(HintType.QUOTE, component.children)
}

private fun FlowContent.hintBlock(type: HintType, children: List<DocComponent>) {
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
        childComponent(children)
    }
}

private fun FlowContent.bulletListComponent(component: BulletListComponent) {
    ul(className = "list-disc list-outside !pl-8 [&_*]:leading-3") {
        listItemComponent(component.children)
    }
}

public fun FlowContent.orderListComponent(component: OrderedListComponent) {
    ol(className = "list-decimal list-outside !pl-8 [&_*]:leading-3") {
        listItemComponent(component.children)
    }
}

private fun FlowTag.listItemComponent(components: List<DocComponent>) {
    components.forEach { component ->
        when (component) {
            is ListItemComponent -> {
                li(className = "py-2") {
                    childComponent(component.children)
                }
            }

            is BulletListComponent -> bulletListComponent(component)
            is OrderedListComponent -> orderListComponent(component)

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

private fun FlowContent.linkComponent(component: LinkComponent) {
    val className = "text-(--project-color) hover:text-(--project-color)/70 transition-colors duration-300 ease-in-out"

    if (!component.destination.startsWith("/")) {
        a(
            className = className,
            href = component.destination,
            target = Target.BLANK,
            rel = "noreferrer",
        ) {
            childComponent(component.children)
        }
        return
    }

    navigate(
        to = "..${component.destination}",
        className = className,
    ) {
        childComponent(component.children)
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

private fun FlowContent.hintComponent(component: HintComponent) {
    hintBlock(component.hintType, component.children)
}

private fun FlowContent.conditionalComponent(component: ConditionalComponent) {
    when (val condition = component.condition) {
        is PlatformCondition -> {}
        is LanguageCondition -> {}
        is BuildToolCondition -> {}
    }
}

