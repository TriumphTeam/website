package dev.triumphteam.frontend.state

import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlin.reflect.KProperty

public class LocalStorageState(private val key: String, public val onUpdate: (value: String) -> Unit) :
    AbstractState<String>(), MutableState<String> {

    private val mutationPolicy = StructureEqualityPolicy<String>()
    private val media = if (window.matchMedia("(prefers-color-scheme: dark)").matches) "dark" else "light"
    private val storageValue = localStorage.getItem(key)

    private var value: String = storageValue ?: media

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        setValue(value)
    }

    internal fun setValue(value: String): Boolean {
        val shouldMutate = mutationPolicy.shouldMutate(this.value, value)

        if (!shouldMutate) return false

        this.value = value
        localStorage.setItem(key, value) // Also write to storage.
        onUpdate(value)
        update()
        return true
    }
}

public inline fun localStorageState(
    key: String,
    noinline onUpdate: (value: String) -> Unit,
): MutableState<String> {
    return LocalStorageState(key, onUpdate)
}

