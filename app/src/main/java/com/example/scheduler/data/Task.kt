package com.example.scheduler.data

/**
 * @param title main title
 * @param description
 * @param timeForReminder save the time for reminder as an integer. this is also the start time
 * @param dateForReminder date for the first reminder
 * @param dateWise this value tells if the task repeats on a particular date of the month or based on the value of [repeatGapDuration]
 * @param repeatGapDuration repeat in _____ days this is for recurring events
 * @param snoozeDuration time delay allowed for reminder within the same day
 * @param postponeDuration how many days we can postpone the task
 * */
data class Task(
    val title: String,
    val description: String,
    val timeForReminder: Int,
    val dateForReminder: Int,
    val dateWise: Boolean,
    val repeatGapDuration: Int,
    val snoozeDuration: Int,
    val postponeDuration: Int,
)

enum class Repetition {
    DAY, WEEK, MONTH, SAME_DATE

}

class Reps(
    val enumValue: Repetition,
    val timeUnit: String,
    val step: Int
)

object Repetitions {
    val DAY = Reps(enumValue = Repetition.DAY, timeUnit = "day", step = 1)
    val WEEK = Reps(enumValue = Repetition.WEEK, timeUnit = "week", step = 7)
    val MONTH = Reps(enumValue = Repetition.MONTH, timeUnit = "month", step = 30)
    val SAME_DATE = Reps(enumValue = Repetition.SAME_DATE, timeUnit = "error", step = 0)
}