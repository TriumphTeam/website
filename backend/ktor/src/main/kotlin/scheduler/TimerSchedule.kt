package dev.triumphteam.backend.scheduler

import java.time.LocalDateTime

/**
 * A schedule that is based on a second timer.
 */
class TimerSchedule(
    private val period: Long,
    private val delay: Long,
    override val repeating: Boolean = true,
    private val block: suspend () -> Unit,
) : Schedule {

    private var delayOver = false
    private var counter = 0L

    override suspend fun execute() {
        block()
    }

    override fun shouldRun(nowDateTime: LocalDateTime): Boolean {
        counter++

        if (!delayOver && (delay == 0L || counter % delay == 0L)) {
            delayOver = true
            return true
        }

        if (!delayOver) return false
        if (period == 0L) return false
        if (counter % period == 0L) return true
        return false
    }

}