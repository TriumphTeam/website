package dev.triumphteam.frontend.state

import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.website.serializable.ContentSection

public class SearchState(private val data: List<ContentSection>) : AbstractState<List<ContentSection>>() {

    public var pattern: String = ""
        private set

    override fun get(): List<ContentSection> {
        if (pattern.length < 3) return emptyList()
        println("bigger pattern")
        return data.filter { item ->
            item.content.lowercase().contains(pattern.lowercase())
        }.also { println("searching ${it.size} results") }
    }

    public fun setPattern(pattern: String) {
        this.pattern = pattern
        update()
    }
}

public fun searchStateOf(data: List<ContentSection>): SearchState {
    return SearchState(data)
}
