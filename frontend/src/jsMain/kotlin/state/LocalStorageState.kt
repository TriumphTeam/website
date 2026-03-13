package dev.triumphteam.frontend.state

import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import kotlinx.browser.localStorage
import kotlin.reflect.KProperty

public class LocalStorageState<T>(
    private val key: String,
    default: T,
    private val transform: (String) -> T?,
    private val reverseTransform: (T) -> String,
) :
    AbstractState<T>(), MutableState<T> {

    private val mutationPolicy = StructureEqualityPolicy<T>()
    private val storageValue = localStorage.getItem(key)
    private val firstValue = default

    private var value: T = storageValue?.let { transform(it) } ?: firstValue

    override fun get(): T {
        return value
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get()
    }

    override fun set(value: T) {
        setValue(value)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(value)
    }

    internal fun setValue(value: T): Boolean {
        val shouldMutate = mutationPolicy.shouldMutate(this.value, value)

        if (!shouldMutate) return false

        this.value = value
        localStorage.setItem(key, reverseTransform(value)) // Also write to storage.
        update()
        return true
    }
}

public inline fun <T> localStorageState(
    key: String,
    default: T,
    noinline transform: (String) -> T?,
    noinline reverseTransform: (T) -> String,
): MutableState<T> {
    return LocalStorageState(key, default, transform, reverseTransform)
}

