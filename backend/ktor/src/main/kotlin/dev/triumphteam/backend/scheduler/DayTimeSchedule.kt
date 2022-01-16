package dev.triumphteam.backend.scheduler

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class DayTimeSchedule(
    private val days: Set<DayOfWeek>,
    private val time: LocalTime,
    override val repeating: Boolean = true,
    private val block: suspend () -> Unit,
) : Schedule {

    override suspend fun execute() {
        block()
    }

    override fun shouldRun(nowDateTime: LocalDateTime): Boolean {
        if (nowDateTime.dayOfWeek !in days) return false
        return nowDateTime.toLocalTime() == time
    }

}