package dev.triumphteam.backend.scheduler

import java.time.LocalDateTime

/**
 * Defines a schedule for a task.
 */
interface Schedule {

    /**
     * Whether the schedule is to be repeated.
     */
    val repeating: Boolean

    /**
     * Executes the schedule's task.
     */
    suspend fun execute()

    /**
     * Checks whether the schedule should run the task.
     */
    fun shouldRun(nowDateTime: LocalDateTime): Boolean

}