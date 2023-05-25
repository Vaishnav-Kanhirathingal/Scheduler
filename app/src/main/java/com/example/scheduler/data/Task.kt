package com.example.scheduler.data

import android.icu.util.Calendar
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
) {
    fun nextReminderIsScheduledIn(): Int {
        // TODO: check for legitimacy
        val calenderInstance = Calendar.getInstance()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = Date(
            dayOfMonth = calenderInstance[Calendar.DAY_OF_MONTH],
            month = calenderInstance[Calendar.MONTH],
            year = calenderInstance[Calendar.YEAR]
        )
        val todayDate = LocalDate.parse(
            StringFunctions.getDateAsText(
                y = today.year,
                m = today.month,
                d = today.dayOfMonth
            ),
            formatter
        )
        if (dateWise) {
            val nextDate = LocalDate.parse(
                StringFunctions.getDateAsText(
                    y = if (today.month == 11 && (today.dayOfMonth > dateForReminder.dayOfMonth)) {
                        today.year + 1
                    } else {
                        today.year
                    },
                    m = if (today.month == 11 && (today.dayOfMonth > dateForReminder.dayOfMonth)) {
                        0
                    } else {
                        today.month + 1
                    },
                    d = dateForReminder.dayOfMonth
                ), formatter
            )
            return Duration.between(todayDate.atStartOfDay(), nextDate.atStartOfDay()).toDays()
                .toInt()
            // TODO: get separate for date wise
        } else {
            val alarmSetOnDate = LocalDate.parse(
                StringFunctions.getDateAsText(
                    y = dateForReminder.year,
                    m = dateForReminder.month,
                    d = dateForReminder.dayOfMonth
                ),
                formatter
            )
            val gap =
                Duration.between(todayDate.atStartOfDay(), alarmSetOnDate.atStartOfDay()).toDays()
            if (repeatGapDuration == 0) {
                // TODO:
                return 10000
            } else {
                (gap % repeatGapDuration).let {
                    return repeatGapDuration - if (it == 0L) {
                        repeatGapDuration
                    } else {
                        it.toInt()
                    }
                }
            }
        }
    }

    fun isScheduledIn(inDays: Int): Boolean {
        return nextReminderIsScheduledIn() <= inDays
    }
}

enum class RepetitionEnum {
    DAY, WEEK, MONTH, SAME_DATE, ALL

}

/** @param step can't be zero */
class Reps(
    val enumValue: RepetitionEnum,
    val timeUnit: String,
    val step: Int
)

object Repetitions {
    val DAY = Reps(enumValue = RepetitionEnum.DAY, timeUnit = "Day", step = 1)
    val WEEK = Reps(enumValue = RepetitionEnum.WEEK, timeUnit = "Week", step = 7)
    val MONTH = Reps(enumValue = RepetitionEnum.MONTH, timeUnit = "Month", step = 30)
    val SAME_DATE = Reps(enumValue = RepetitionEnum.SAME_DATE, timeUnit = "Date", step = 1)
    val ALL = Reps(enumValue = RepetitionEnum.ALL, timeUnit = "All", step = 1)
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
        dateForReminder = Date(dayOfMonth = 25, month = 5, year = 2023),
        dateWise = false,
        repeatGapDuration = 7,
        snoozeDuration = 10,
        postponeDuration = 15
    ),
    Task(
        title = "Birthday",
        description = "Buy a gift",
        timeForReminder = Time(hour = 18, minute = 0),
        dateForReminder = Date(dayOfMonth = 24, month = 5, year = 2023),
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