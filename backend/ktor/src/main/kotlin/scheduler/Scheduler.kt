package dev.triumphteam.backend.scheduler

import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.server.application.install
import io.ktor.server.application.pluginOrNull
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.EnumSet
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Simple coroutine based scheduler.
 */
class Scheduler : CoroutineScope {

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + dispatcher

    private val schedules = mutableListOf<Schedule>()
    private var clock = Clock.systemDefaultZone()
    private var started = false

    /**
     * Runs a task at a given date time.
     */
    fun runTaskAt(date: LocalDateTime, action: suspend () -> Unit) {
        schedules.add(DateSchedule(date, action))
    }

    /**
     * Runs task after a given duration.
     */
    fun runTaskIn(time: Duration, task: suspend () -> Unit) {
        schedules.add(TimerSchedule(0, time.inWholeSeconds, false, task))
    }

    /**
     * Runs task every `x` duration after `y` duration.
     */
    fun runTaskEvery(period: Duration, delay: Duration, task: suspend () -> Unit) {
        schedules.add(TimerSchedule(period.inWholeSeconds, delay.inWholeSeconds, true, task))
    }

    /**
     * Runs task every given days at a given time.
     */
    fun runTaskEvery(days: Set<DayOfWeek>, at: LocalTime, task: suspend () -> Unit) {
        schedules.add(DayTimeSchedule(days, at, true, task))
    }

    /**
     * Start the timer logic.
     */
    private fun start(): Scheduler {
        launch {
            var lastCheck: LocalDateTime? = null

            while (true) {
                val second = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS)

                if (lastCheck != second) {
                    lastCheck = second

                    coroutineScope {
                        launchSchedules(second)
                    }

                }

                delay(50)
            }
        }

        started = true
        return this
    }

    /**
     * Launches each schedule.
     */
    private fun launchSchedules(nowMinute: LocalDateTime) {
        schedules.forEach {
            launch {
                if (!it.shouldRun(nowMinute)) return@launch

                it.execute()

                if (!it.repeating) schedules.remove(it)
            }
        }
    }

    /**
     * Feature companion, which is a factory for the [Scheduler].
     */
    companion object Feature : Plugin<Application, Scheduler, Scheduler> {

        /**
         * The locale [AttributeKey].
         */
        override val key: AttributeKey<Scheduler> = AttributeKey("scheduler")

        /**
         * Installation function to create a [Scheduler] feature.
         */
        override fun install(pipeline: Application, configure: Scheduler.() -> Unit): Scheduler {
            return Scheduler().apply(configure).apply { start() }
        }
    }

}

/**
 * Runs task after a given duration.
 */
fun Application.runTaskIn(time: Duration, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskIn(time, task)
}

/**
 * Runs a task at a given date time.
 */
fun Application.runTaskAt(date: LocalDateTime, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskAt(date, task)
}

/**
 * Runs a task at a given time.
 */
fun Application.runTaskAt(time: LocalTime, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskAt(time.atDate(LocalDateTime.now().toLocalDate()), task)
}

/**
 * Runs task every `x` duration after `y` duration.
 */
fun Application.runTaskEvery(period: Duration, delay: Duration = 0.seconds, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskEvery(period, delay, task)
}

/**
 * Runs task every given days at a given time.
 */
fun Application.runTaskEvery(days: Set<DayOfWeek>, at: LocalTime, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskEvery(days, at, task)
}

/**
 * Runs task every given days at a given time.
 */
fun Application.runTaskEvery(vararg days: DayOfWeek, at: LocalTime, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskEvery(days.toCollection(EnumSet.noneOf(DayOfWeek::class.java)), at, task)
}

/**
 * Runs task every given day at a given time.
 */
fun Application.runTaskEvery(day: DayOfWeek, at: LocalTime, task: suspend () -> Unit) {
    val scheduler = pluginOrNull(Scheduler) ?: install(Scheduler)
    return scheduler.runTaskEvery(EnumSet.of(day), at, task)
}
