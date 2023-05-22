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
    val timeForReminder: Time,
    val dateForReminder: Date,
    val dateWise: Boolean,
    val repeatGapDuration: Int,
    val snoozeDuration: Int,
    val postponeDuration: Int,
)

enum class Repetition {
    DAY, WEEK, MONTH, SAME_DATE

}

/** @param step can't be zero */
class Reps(
    val enumValue: Repetition,
    val timeUnit: String,
    val step: Int
)

object Repetitions {
    val DAY = Reps(enumValue = Repetition.DAY, timeUnit = "day", step = 1)
    val WEEK = Reps(enumValue = Repetition.WEEK, timeUnit = "week", step = 7)
    val MONTH = Reps(enumValue = Repetition.MONTH, timeUnit = "month", step = 30)
    val SAME_DATE = Reps(enumValue = Repetition.SAME_DATE, timeUnit = "error", step = 1)
}

class Date(
    val dayOfMonth: Int,
    val month: Int,
    val year: Int
)

class Time(
    val hour: Int,
    val minute: Int
)

object StringFunctions {
    /** gets value like 6,1 and formats it as 06:01 AM */
    fun getTimeAsText(hour: Int, minute: Int): String {
        val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
        return "${if (hour > 12) (hour - 12).toString() else hour.toString()}:${t(minute)} ${if (hour > 12) "PM" else "AM"}"
    }

    /** gets value like 2021,1,1 and formats it as 01/01/2021 */
    fun getDateAsText(y: Int, m: Int, d: Int): String {
        val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
        return "${t(d)}/${t(m)}/${t(y)}"
    }

    /** gets value like 2 or 12 and returns 2nd ar 12th */
    fun numFormatter(num: Int): String {
        return num.toString() +
                if (num < 10 || num > 20) {
                    when (num % 10) {
                        1 -> "st"
                        2 -> "nd"
                        3 -> "rd"
                        else -> "th"
                    }
                } else {
                    "th"
                }
    }

    fun getTextWithS(unit: String, num: Int): String {
        return "$num $unit" + if (num > 1) "s" else ""
    }
}

/** delete this object, do not use in finished project */
val testTaskList = mutableListOf(
    Task(
        title = "Meeting",
        description = "Discuss project updates",
        timeForReminder = Time(hour = 9, minute = 30),
        dateForReminder = Date(dayOfMonth = 15, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 7,
        snoozeDuration = 10,
        postponeDuration = 15
    ),
    Task(
        title = "Birthday",
        description = "Buy a gift",
        timeForReminder = Time(hour = 18, minute = 0),
        dateForReminder = Date(dayOfMonth = 10, month = 9, year = 2023),
        dateWise = true,
        repeatGapDuration = 0,
        snoozeDuration = 5,
        postponeDuration = 10
    ),
    Task(
        title = "Dentist Appointment",
        description = "Get a dental checkup",
        timeForReminder = Time(hour = 14, minute = 30),
        dateForReminder = Date(dayOfMonth = 25, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 0,
        snoozeDuration = 15,
        postponeDuration = 30
    ),
    Task(
        title = "Meeting",
        description = "Discuss project updates",
        timeForReminder = Time(hour = 9, minute = 30),
        dateForReminder = Date(dayOfMonth = 15, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 7,
        snoozeDuration = 10,
        postponeDuration = 15
    ),
    Task(
        title = "Birthday",
        description = "Buy a gift",
        timeForReminder = Time(hour = 18, minute = 0),
        dateForReminder = Date(dayOfMonth = 10, month = 9, year = 2023),
        dateWise = true,
        repeatGapDuration = 0,
        snoozeDuration = 5,
        postponeDuration = 10
    ),
    Task(
        title = "Dentist Appointment",
        description = "Get a dental checkup",
        timeForReminder = Time(hour = 14, minute = 30),
        dateForReminder = Date(dayOfMonth = 25, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 0,
        snoozeDuration = 15,
        postponeDuration = 30
    ),
    Task(
        title = "Gym Workout",
        description = "Cardio and strength training",
        timeForReminder = Time(hour = 8, minute = 0),
        dateForReminder = Date(dayOfMonth = 22, month = 5, year = 2023),
        dateWise = true,
        repeatGapDuration = 7,
        snoozeDuration = 5,
        postponeDuration = 10
    ),
    Task(
        title = "Project Deadline",
        description = "Submit final report",
        timeForReminder = Time(hour = 12, minute = 0),
        dateForReminder = Date(dayOfMonth = 30, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 0,
        snoozeDuration = 0,
        postponeDuration = 0
    ),
    Task(
        title = "Shopping",
        description = "Buy groceries",
        timeForReminder = Time(hour = 10, minute = 0),
        dateForReminder = Date(dayOfMonth = 23, month = 5, year = 2023),
        dateWise = true,
        repeatGapDuration = 7,
        snoozeDuration = 10,
        postponeDuration = 20
    ),
    Task(
        title = "Meeting",
        description = "Discuss project updates",
        timeForReminder = Time(hour = 9, minute = 30),
        dateForReminder = Date(dayOfMonth = 15, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 7,
        snoozeDuration = 10,
        postponeDuration = 15
    ),
    Task(
        title = "Movie Night",
        description = "Watch a new release",
        timeForReminder = Time(hour = 20, minute = 30),
        dateForReminder = Date(dayOfMonth = 27, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 0,
        snoozeDuration = 0,
        postponeDuration = 0
    ),
    Task(
        title = "Anniversary",
        description = "Plan a surprise",
        timeForReminder = Time(hour = 18, minute = 0),
        dateForReminder = Date(dayOfMonth = 10, month = 6, year = 2023),
        dateWise = false,
        repeatGapDuration = 0,
        snoozeDuration = 15,
        postponeDuration = 30
    ),
    Task(
        title = "Doctor's Appointment",
        description = "Follow-up on test results",
        timeForReminder = Time(hour = 11, minute = 30),
        dateForReminder = Date(dayOfMonth = 5, month = 6, year = 2023),
        dateWise = false,
        repeatGapDuration = 0,
        snoozeDuration = 0,
        postponeDuration = 0
    ),
    Task(
        title = "Conference Call",
        description = "Discuss upcoming projects",
        timeForReminder = Time(hour = 16, minute = 0),
        dateForReminder = Date(dayOfMonth = 8, month = 6, year = 2023),
        dateWise = true,
        repeatGapDuration = 14,
        snoozeDuration = 10,
        postponeDuration = 20
    )
)