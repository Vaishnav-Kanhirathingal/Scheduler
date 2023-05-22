package com.example.scheduler.firebase

object FirebaseConstants {
    const val parentCollectionName = "users"
    const val listOfTaskName = "taskList"
    object task {
        const val title = "title"
        const val description = "description"
        const val timeForReminderHour="timeForReminderHour"
        const val timeForReminderMinute="timeForReminderMinute"
        const val dateForReminderDay="dateForReminderDay"
        const val dateForReminderMonth="dateForReminderMonth"
        const val dateForReminderYear="dateForReminderYear"
        const val dateWise = "dateWise"
        const val repeatGapDuration = "repeatGapDuration"
        const val snoozeDuration = "snoozeDuration"
        const val postponeDuration = "postponeDuration"
    }
}
