package dev.triumphteam.frontend.state

import dev.triumphteam.frontend.internal.IntersectionObserver
import dev.triumphteam.horizon.component.ReactiveElement
import dev.triumphteam.horizon.component.functional.FunctionalComponent
import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.State
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.asList
import kotlin.reflect.KProperty

public class SectionsObserverState : AbstractState<Set<String>>() {

    private val mutationPolicy = StructureEqualityPolicy<Set<String>>()
    private val allElements = document.querySelectorAll("#doc-section a").asList()
        .mapNotNull { it.lastChild as? Element }
        .filter { it.tagName == "H1" || it.tagName == "H2" }
        .associateBy { it.id }

    private var visibleElements = setOf<String>()

    private val intersectionObserver = IntersectionObserver(
        callback = { entries, observer ->
            val mutableEntries = visibleElements.toMutableSet()

            entries.forEach { entry ->
                if (entry.isIntersecting) {
                    mutableEntries.add(entry.target.id)
                    return@forEach
                }

                mutableEntries.remove(entry.target.id)
            }

            setValue(mutableEntries.filter { it in allElements }.toSet())
        },
        options = js("{root: null, rootMargin: '0px', threshold: [1.0]}"),
    ).apply {
        allElements.values.forEach { observe(it) }
    }

    override fun get(): Set<String> {
        return visibleElements
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> {
        return get()
    }

    override fun onRemove(element: ReactiveElement) {
        intersectionObserver.disconnect()
    }

    private fun setValue(value: Set<String>) {
        val shouldMutate = mutationPolicy.shouldMutate(visibleElements, value)
        if (!shouldMutate) return

        visibleElements = value
        update()
    }
}

public fun FunctionalComponent.rememberSectionsState(): State<Set<String>> = remember(SectionsObserverState())
