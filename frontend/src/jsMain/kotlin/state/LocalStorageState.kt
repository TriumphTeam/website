package dev.triumphteam.frontend.state

import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.MutableState
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import kotlinx.browser.localStorage
import kotlin.reflect.KProperty

public class LocalStorageState(private val key: String, values: List<String>) :
    AbstractState<String>(), MutableState<String> {

    private val mutationPolicy = StructureEqualityPolicy<String>()
    private val storageValue = localStorage.getItem(key)
    private val firstValue = values.first()

    private var value: String = when (storageValue) {
        null -> firstValue
        in values -> storageValue
        else -> firstValue
    }

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
        update()
        return true
    }
}

public inline fun localStorageState(key: String, values: List<String>): MutableState<String> {
    return LocalStorageState(key, values)
}

