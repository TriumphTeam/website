package dev.triumphteam.frontend.state

import dev.triumphteam.horizon.component.functional.FunctionalComponent
import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.State
import dev.triumphteam.horizon.state.policy.StateMutationPolicy
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

public class ApiCallState<T : Any>(
    private val block: suspend () -> T,
    private val parentCoroutine: CoroutineScope,
    private val mutationPolicy: StateMutationPolicy<T>,
) : AbstractState<ApiResult<T>>() {

    private var value: T? = null
    private var job: Job? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): ApiResult<T> {
        val value = value
        // First, check if there is already a value, if there is then no point in waiting, just return it.
        if (value != null) return Success(value)

        // If we are currently running a job, then we are waiting for it to complete.
        val job = job
        if (job != null && job.isActive) return Waiting()

        // If there are no jobs, re launch a new one.
        parentCoroutine.launch {
            delay(3000)
            setValue(block())
        }

        return Waiting()
    }

    private fun setValue(value: T): Boolean {
        val shouldMutate = mutationPolicy.shouldMutate(this.value, value)

        if (!shouldMutate) return false

        this.value = value
        update()
        return true
    }
}

public sealed interface ApiResult<T : Any>
public data class Success<T : Any>(public val data: T) : ApiResult<T>
public class Waiting<T : Any> : ApiResult<T>

public inline fun <T : Any> ApiResult<T>.fold(
    onSuccess: Success<T>.() -> Unit,
    onWaiting: Waiting<T>.() -> Unit,
): Unit = when (this) {
    is Success -> onSuccess()
    is Waiting -> onWaiting()
}

public fun <T : Any> FunctionalComponent.rememberApiCallState(
    mutationPolicy: StateMutationPolicy<T> = StructureEqualityPolicy(),
    block: suspend () -> T,
): State<ApiResult<T>> {
    return remember(
        state = ApiCallState(
            block = block,
            parentCoroutine = this,
            mutationPolicy = mutationPolicy,
        ),
    )
}
