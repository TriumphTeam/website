package dev.triumphteam.frontend.pages.docs.components.sidebar

import dev.triumphteam.frontend.api.api
import dev.triumphteam.frontend.state.fold
import dev.triumphteam.frontend.state.rememberApiCallState
import dev.triumphteam.frontend.state.searchStateOf
import dev.triumphteam.horizon.component.functional.component
import dev.triumphteam.horizon.html.FlowContent
import dev.triumphteam.horizon.html.FlowTag
import dev.triumphteam.horizon.html.div
import dev.triumphteam.horizon.html.i
import dev.triumphteam.horizon.html.input
import dev.triumphteam.horizon.html.link
import dev.triumphteam.horizon.html.span
import dev.triumphteam.horizon.router.navigate
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.website.serializable.ContentSection
import dev.triumphteam.website.serializable.SEARCH_DATA_ROUTE
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

private const val SEARCH_CLASS = "flex bg-dark-surface border border-white/10 rounded-lg"

public fun FlowContent.searchButton(searchOpenedState: MutableState<Boolean>) {
    div(className = "cursor-pointer") {
        onClick = {
            println("search button clicked ${searchOpenedState.get()}")
            searchOpenedState.set(true)
        }

        div(className = "$SEARCH_CLASS items-center w-full h-12") {
            div(className = "w-full") {
                span(className = "w-full px-4 py-1 text-white rounded-full focus:outline-none pointer-events-none text-white/50 select-none") {
                    text("Search")
                }
            }
            div {
                div(className = "flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg") {
                    i(className = "w-5 h-5 bx bx-search")
                }
            }
        }
    }
}

public fun FlowContent.searchArea(projectVersion: String, searchOpenedState: MutableState<Boolean>) {
    component {
        var opened by remember(searchOpenedState)

        render {
            val classes = if (opened) "opacity-100 pointer-events-auto" else "opacity-0 pointer-events-none"

            div(className = "$classes search-area fixed inset-0 bg-black/50 backdrop-blur-md z-100 flex items-start justify-center pt-48") {
                onClick = { event ->
                    opened = false
                    event.stopPropagation()
                }

                component {
                    val result by rememberApiCallState {
                        api.get(SEARCH_DATA_ROUTE) {
                            parameter("version", projectVersion)
                        }.body<List<ContentSection>>()
                    }

                    val action: (Event) -> Unit = action@{ event ->
                        if (event !is KeyboardEvent) return@action
                        if (event.key != "Escape") return@action
                        opened = false
                    }

                    onCreate {
                        document.addEventListener("keydown", action)
                    }

                    onDestroy {
                        document.removeEventListener("keydown", action)
                    }

                    render {
                        div {
                            result.fold(
                                onSuccess = {
                                    searchArea(searchOpenedState, data)
                                },
                                onWaiting = {},
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun FlowContent.searchArea(searchOpenedState: MutableState<Boolean>, data: List<ContentSection>) {
    val searchState = searchStateOf(data)

    div(className = "flex flex-col gap-4 items-center justify-center w-lg h-4/5 relative") {
        onClick = Event::stopPropagation

        div(className = "$SEARCH_CLASS items-center w-full") {
            div(className = "w-full") {
                input(className = "w-full px-4 py-1 text-white rounded-full focus:outline-none text-white/50 select-none") {
                    placeholder = "Search"
                    onInput = onInput@{ event ->
                        val target = event.target as? HTMLInputElement ?: return@onInput
                        searchState.setPattern(target.value)
                    }
                }
            }
            div {
                div(className = "flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg") {
                    i(className = "w-5 h-5 bx bx-search")
                }
            }
        }

        component {

            val searchResult by remember(searchState)

            render {
                div(className = "$SEARCH_CLASS w-full min-h-48 max-h-[480px] overflow-y-auto absolute top-full mt-4 left-0 right-0 z-10 pb-4") {
                    div(className = "w-full flex flex-col") {
                        if (searchResult.isEmpty()) {
                            emptyResult()
                            return@div
                        }
                        searchResult
                            .groupBy { it.pageId }
                            .forEach { (_, results) ->
                                val first = results.firstOrNull() ?: return@forEach

                                div(className = "") {
                                    div(className = "px-4 py-3 bg-dark-surface/50") {
                                        div(className = "text-xs font-semibold text-white/50 uppercase tracking-wide") {
                                            text(first.title)
                                        }
                                    }

                                    div(className = "flex flex-col gap-2 px-4") {
                                        results.forEach { result ->
                                            navigate(
                                                to = "../${result.pageId}#${result.sectionId}",
                                                beforeNavigate = {
                                                    searchOpenedState.set(false)
                                                },
                                                className = "border border-white/10 rounded-lg px-4 py-3 hover:bg-dark-background cursor-pointer transition-colors",
                                            ) {

                                                div(className = "flex flex-row gap-3 items-center") {
                                                    div(className = "flex-shrink-0 flex pt-0.5") {
                                                        i(className = "bx bx-hashtag text-white/30 text-xs")
                                                    }

                                                    div(className = "flex flex-col gap-1 flex-1 min-w-0") {
                                                        div(className = "text-sm font-medium text-white") {
                                                            text(result.section)
                                                        }
                                                        div(className = "text-xs text-white/50 line-clamp-2") {
                                                            contentWithMatches(result.content, searchState.pattern)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
}

private fun FlowContent.emptyResult() {
    div(className = "w-full h-full flex justify-center items-center text-white/50 text-center") {
        div {
            text("No results found")
        }
    }
}

private fun FlowTag.contentWithMatches(content: String, pattern: String) {
    val maxLength = 200

    if (pattern.length < 3) {
        text(content.take(maxLength))
        return
    }

    val firstMatchIndex = content.indexOf(pattern, ignoreCase = true)

    if (firstMatchIndex == -1) {
        text(content.take(maxLength))
        return
    }

    val startPos = when {
        firstMatchIndex > maxLength / 2 -> (firstMatchIndex - maxLength / 2).coerceAtLeast(0)
        else -> 0
    }

    val endPos = (startPos + maxLength).coerceAtMost(content.length)
    val snippet = content.substring(startPos, endPos)

    if (startPos > 0) text("... ")

    val words = pattern.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

    val regex = Regex(words.joinToString("|", transform = Regex.Companion::escape), RegexOption.IGNORE_CASE)

    var lastIndex = 0
    regex.findAll(snippet).forEach { match ->
        if (match.range.first > lastIndex) {
            text(snippet.substring(lastIndex, match.range.first))
        }

        span(className = "font-bold") {
            text(match.value)
        }

        lastIndex = match.range.last + 1
    }

    if (lastIndex < snippet.length) {
        text(snippet.substring(lastIndex))
    }

    if (endPos < content.length) text(" ...")
}
