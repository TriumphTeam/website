package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.h1
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.horizon.state.State
import dev.triumphteam.website.serializable.NavigationPage
import dev.triumphteam.website.serializable.VersionDocument
import org.w3c.dom.Element

public fun FlowContent.navigationArea(
    pageState: State<String>,
    document: VersionDocument,
    popoverElement: Element? = null,
) {
    div(className = "px-4 overflow-y-auto overflow-x-hidden overscroll-contain grow") {
        div(className = "grid grid-cols-1 gap-10 text-dark-text-secondary") {
            document.groups.forEach { group ->
                navigationGroup(
                    pageState = pageState,
                    text = group.name,
                    pages = group.pages,
                    element = popoverElement,
                )
            }
        }
    }
}

private fun FlowContent.navigationGroup(
    pageState: State<String>,
    text: String,
    pages: List<NavigationPage>,
    element: Element? = null,
) {
    div {
        h1(className = "text-dark-text-primary text-2xl xl:text-lg 2xl:text-xl font-bold") {
            text(text)
        }
        component {
            val currentPage by remember(pageState)

            render {
                pages.forEach { page ->
                    navigationLink(
                        text = page.name,
                        link = page.id,
                        selected = currentPage == page.id,
                        element = element,
                    )
                }
            }
        }
    }
}

private fun FlowContent.navigationLink(
    text: String,
    link: String,
    selected: Boolean,
    element: Element? = null,
) {
    val color = if (selected) "text-(--project-color)" else ""

    div(className = "pt-2 $color") {
        navigate(
            to = "../$link",
            className = "xl:text-base 2xl:text-lg hover:text-(--project-color) transition ease-in-out",
            beforeNavigate = {
                element?.asDynamic()?.hidePopover()
            },
        ) {
            text(text)
        }
    }
}
