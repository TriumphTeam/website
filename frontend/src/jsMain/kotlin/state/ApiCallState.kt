package dev.triumphteam.frontend.state

import dev.triumphteam.horizon.component.functional.FunctionalComponent
import dev.triumphteam.horizon.state.AbstractState
import dev.triumphteam.horizon.state.State
import dev.triumphteam.horizon.state.policy.StateMutationPolicy
import dev.triumphteam.horizon.state.policy.StructureEqualityPolicy
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty
import kotlin.time.Duration.Companion.seconds

public class ApiCallState<T : Any>(
    private val parentCoroutine: CoroutineScope,
    private val test: () -> T,
    private val mutationPolicy: StateMutationPolicy<T>,
) : AbstractState<ApiResult<T>>() {

    private companion object {
        private val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    private var value: T? = null
    private var job: Job? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): ApiResult<T> {
        val value = value
        if (value != null) return Success(value)

        val job = job

        if (job != null && job.isActive) return Waiting()

        parentCoroutine.launch {
            delay(5.seconds)
            setValue(test())
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
public data class Error<T : Any>(public val error: Throwable) : ApiResult<T>

public inline fun <T : Any> ApiResult<T>.fold(
    onSuccess: Success<T>.() -> Unit,
    onFailure: Error<T>.() -> Unit,
    onWaiting: Waiting<T>.() -> Unit,
): Unit = when (this) {
    is Success -> onSuccess()
    is Error -> onFailure()
    is Waiting -> onWaiting()
}

public fun <T : Any> FunctionalComponent.rememberApiCallState(
    test: () -> T,
    mutationPolicy: StateMutationPolicy<T> = StructureEqualityPolicy(),
): State<ApiResult<T>> {
    return remember(ApiCallState(this, test, mutationPolicy))
}
