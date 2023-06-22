package com.example.scheduler.data

import com.example.scheduler.firebase.FirebaseKeys
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * @param title main title
 * @param description
 * @param timeForReminder save the time for reminder as an integer. this is also the start time
 * @param dateForReminder date for the first reminder
 * @param dateWise this value tells if the task repeats on a particular date of the month or based on the value of [repeatGapDuration]
 * @param repeatGapDuration repeat in _____ days this is for recurring events
 * @param postponeDuration how many days we can postpone the task
 * */
data class Task(
    val title: String,
    val description: String,

    val timeForReminder: Time,
    val dateForReminder: Date,

    val dateWise: Boolean,
    val repeatGapDuration: Int,

    val postponeDuration: Int,
) {
    companion object {
        fun fromDocument(i: DocumentSnapshot): Task {
            return Task(
                title = i[FirebaseKeys.TaskName.title].toString(),
                description = i[FirebaseKeys.TaskName.description].toString(),
                timeForReminder = Time(
                    hour = i[FirebaseKeys.TaskName.timeForReminderHour].toString().toInt(),
                    minute = i[FirebaseKeys.TaskName.timeForReminderMinute].toString().toInt(),
                ),
                dateForReminder = Date(
                    dayOfMonth = i[FirebaseKeys.TaskName.dateForReminderDay].toString().toInt(),
                    month = i[FirebaseKeys.TaskName.dateForReminderMonth].toString().toInt(),
                    year = i[FirebaseKeys.TaskName.dateForReminderYear].toString().toInt(),
                ),
                dateWise = i[FirebaseKeys.TaskName.dateWise].toString().toBoolean(),
                repeatGapDuration = i[FirebaseKeys.TaskName.repeatGapDuration].toString().toInt(),
                postponeDuration = i[FirebaseKeys.TaskName.postponeDuration].toString().toInt(),
            )
        }
    }

    fun toHashMap(): HashMap<String, Serializable> {
        return hashMapOf(
            FirebaseKeys.TaskName.title to this.title,
            FirebaseKeys.TaskName.description to this.description,
            FirebaseKeys.TaskName.timeForReminderHour to this.timeForReminder.hour,
            FirebaseKeys.TaskName.timeForReminderMinute to this.timeForReminder.minute,
            FirebaseKeys.TaskName.dateForReminderDay to this.dateForReminder.dayOfMonth,
            FirebaseKeys.TaskName.dateForReminderMonth to this.dateForReminder.month,
            FirebaseKeys.TaskName.dateForReminderYear to this.dateForReminder.year,
            FirebaseKeys.TaskName.dateWise to this.dateWise,
            FirebaseKeys.TaskName.repeatGapDuration to this.repeatGapDuration,
            FirebaseKeys.TaskName.postponeDuration to this.postponeDuration,
        )
    }

    fun getDaysTillNextReminder(): Int {
        val startDate = LocalDate
            .of(dateForReminder.year, dateForReminder.month, dateForReminder.dayOfMonth)
            .atTime(timeForReminder.hour, timeForReminder.minute)
        val today = LocalDate.now()

        if (repeatGapDuration == 0) {
            val startDateDate = startDate.toLocalDate()
            val diff = ChronoUnit.DAYS.between(today, startDateDate)
            return  diff.toInt()
        } else {
            if (dateWise) {
                val nextMonth =
                    if (today.month.value == 12 && today.dayOfMonth > startDate.dayOfMonth) {
                        1
                    } else if (today.dayOfMonth > startDate.dayOfMonth) {
                        today.month.value + 1
                    } else {
                        today.month.value
                    }
                val nextYear =
                    if (today.month.value == 12 && today.dayOfMonth > startDate.dayOfMonth) {
                        today.year + 1
                    } else {
                        today.year
                    }
                val nextDate = LocalDate
                    .of(nextYear, nextMonth, startDate.dayOfMonth)
                    .atTime(timeForReminder.hour, timeForReminder.minute)
                return ChronoUnit.DAYS.between(today, nextDate.toLocalDate()).toInt()
            } else {
                val startDateDate = startDate.toLocalDate()
                val diff = ChronoUnit.DAYS.between(startDateDate, today)
                return (repeatGapDuration - (diff % repeatGapDuration)).toInt()
            }
        }
    }

    fun isScheduledIn(inDays: Int): Boolean {
        val x = getDaysTillNextReminder()
        return if (x < 0) {
            false
        } else
            x <= inDays
    }

    fun isScheduledForToday(): Boolean {
        val x = getDaysTillNextReminder()
        return if (x < 0) {
            false
        } else
            x == repeatGapDuration
    }

    @Throws(Exception::class)
    fun getTimeRemainingTillReminder(): Time {
        if (isScheduledForToday()) {
            val scheduled =
                LocalDate.now().atTime(timeForReminder.hour, timeForReminder.minute, 0, 0)
            val timeNow = LocalDateTime.now()
            val hoursLeft = scheduled.hour - timeNow.hour
            val minutesLeft = scheduled.minute - timeNow.minute
            return if (hoursLeft < 0 || (hoursLeft == 0 && minutesLeft < 0)) {
                throw Exception("time for reminder has already elapsed")
            } else {
                if (minutesLeft < 0) {
                    Time(hoursLeft - 1, 60 + minutesLeft)
                } else {
                    Time(hoursLeft, minutesLeft)
                }
            }
        } else {
            throw Exception("Task isn't scheduled for today")
        }
    }
}

fun main() {
    Task(
        title = "Meeting",
        description = "Discuss project updates",
        timeForReminder = Time(hour = 15, minute = 30),
        dateForReminder = Date(dayOfMonth = 5, month = 6, year = 2023),
        dateWise = false,
        repeatGapDuration = 8,
        postponeDuration = 15
    )
//        .let {
    TestValues.testTaskList.forEach {
        if (it.repeatGapDuration == 0) {
            println(
                "start = " +
                        StringFunctions.getDateAsText(
                            y = it.dateForReminder.year,
                            m = it.dateForReminder.month,
                            d = it.dateForReminder.dayOfMonth
                        ) +
                        "\t rep = ${it.repeatGapDuration}\tdate wise = ${it.dateWise}\t rem = " +
                        it.getDaysTillNextReminder().toString()+"\tscheduled 7 = ${it.isScheduledIn(7)}"
            )
        }
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
    /**@param hour hour
     * @param minute minute
     *  @return takes value like 6,1 and formats it as 06:01 AM
     */
    fun getTimeAsText(hour: Int, minute: Int): String {
        val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
        return "${if (hour > 12) (hour - 12).toString() else hour.toString()}:${t(minute)} ${if (hour > 12) "PM" else "AM"}"
    }

    /**
     * @param y year
     * @param m month
     * @param d day of month
     * @return takes value like 2021,1,1 and formats it as 01/01/2021
     */
    fun getDateAsText(y: Int, m: Int, d: Int): String {
        val t = { i: Int -> if (i < 10) "0$i" else i.toString() }
        return "${t(d)}/${t(m)}/${t(y)}"
    }

    /**
     * @param num a number
     * @return takes value like 2 or 12 and returns 2nd ar 12th
     */
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

    /**
     * @param unit takes string which is a unit of measure. ex - `day`
     * @param num magnitude of the unit
     * @return if a number is greater than 1, it returns the given string with s. for example, if
     * [unit] and [num] are "day" and 5, it returns 5 days
     */
    fun getTextWithS(unit: String, num: Int): String {
        return "$num $unit" + if (num > 1) "s" else ""
    }
}
