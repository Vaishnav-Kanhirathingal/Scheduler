package com.example.scheduler.firebase

/** this contains the key strings to the data stored in the fire-store
 */
object FirebaseKeys {
    const val parentCollectionName = "users"
    const val listOfTaskName = "taskList"

    /** this contains the keys for every attribute of the Task class
     */
    object TaskName {
        const val title = "title"
        const val description = "description"
        const val timeForReminderHour = "timeForReminderHour"
        const val timeForReminderMinute = "timeForReminderMinute"
        const val dateForReminderDay = "dateForReminderDay"
        const val dateForReminderMonth = "dateForReminderMonth"
        const val dateForReminderYear = "dateForReminderYear"
        const val dateWise = "dateWise"
        const val repeatGapDuration = "repeatGapDuration"
        const val postponeDuration = "postponeDuration"
    }
}