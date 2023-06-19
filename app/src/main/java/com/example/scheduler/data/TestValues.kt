package com.example.scheduler.data

/** delete this object, do not use in finished project */
object TestValues {
    val testTaskList = mutableListOf(
        Task(
            title = "Meeting",
            description = "Discuss project updates",
            timeForReminder = Time(hour = 9, minute = 30),
            dateForReminder = Date(dayOfMonth = 25, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 47,
            postponeDuration = 15
        ),
        Task(
            title = "Birthday",
            description = "Buy a gift",
            timeForReminder = Time(hour = 18, minute = 0),
            dateForReminder = Date(dayOfMonth = 24, month = 5, year = 2023),
            dateWise = true,
            repeatGapDuration = 40,
            postponeDuration = 10
        ),
        Task(
            title = "Dentist Appointment",
            description = "Get a dental checkup",
            timeForReminder = Time(hour = 14, minute = 30),
            dateForReminder = Date(dayOfMonth = 25, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 19,
            postponeDuration = 30
        ),
        Task(
            title = "Meeting",
            description = "Discuss project updates",
            timeForReminder = Time(hour = 9, minute = 30),
            dateForReminder = Date(dayOfMonth = 15, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 7,
            postponeDuration = 15
        ),
        Task(
            title = "Birthday",
            description = "Buy a gift",
            timeForReminder = Time(hour = 18, minute = 0),
            dateForReminder = Date(dayOfMonth = 10, month = 9, year = 2023),
            dateWise = true,
            repeatGapDuration = 8,
            postponeDuration = 10
        ),
        Task(
            title = "Dentist Appointment",
            description = "Get a dental checkup",
            timeForReminder = Time(hour = 14, minute = 30),
            dateForReminder = Date(dayOfMonth = 25, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 54,
            postponeDuration = 30
        ),
        Task(
            title = "Gym Workout",
            description = "Cardio and strength training",
            timeForReminder = Time(hour = 8, minute = 0),
            dateForReminder = Date(dayOfMonth = 22, month = 5, year = 2023),
            dateWise = true,
            repeatGapDuration = 20,
            postponeDuration = 10
        ),
        Task(
            title = "Project Deadline",
            description = "Submit final report",
            timeForReminder = Time(hour = 12, minute = 0),
            dateForReminder = Date(dayOfMonth = 30, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 27,
            postponeDuration = 0
        ),
        Task(
            title = "Shopping",
            description = "Buy groceries",
            timeForReminder = Time(hour = 10, minute = 0),
            dateForReminder = Date(dayOfMonth = 23, month = 5, year = 2023),
            dateWise = true,
            repeatGapDuration = 7,
            postponeDuration = 20
        ),
        Task(
            title = "Meeting",
            description = "Discuss project updates",
            timeForReminder = Time(hour = 9, minute = 30),
            dateForReminder = Date(dayOfMonth = 15, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 50,
            postponeDuration = 15
        ),
        Task(
            title = "Movie Night",
            description = "Watch a new release",
            timeForReminder = Time(hour = 20, minute = 30),
            dateForReminder = Date(dayOfMonth = 27, month = 5, year = 2023),
            dateWise = false,
            repeatGapDuration = 8,
            postponeDuration = 0
        ),
        Task(
            title = "Anniversary",
            description = "Plan a surprise",
            timeForReminder = Time(hour = 18, minute = 0),
            dateForReminder = Date(dayOfMonth = 10, month = 6, year = 2023),
            dateWise = false,
            repeatGapDuration = 6,
            postponeDuration = 30
        ),
        Task(
            title = "Doctor's Appointment",
            description = "Follow-up on test results",
            timeForReminder = Time(hour = 11, minute = 30),
            dateForReminder = Date(dayOfMonth = 5, month = 6, year = 2023),
            dateWise = false,
            repeatGapDuration = 0,
            postponeDuration = 0
        ),
        Task(
            title = "Conference Call",
            description = "Discuss upcoming projects",
            timeForReminder = Time(hour = 16, minute = 0),
            dateForReminder = Date(dayOfMonth = 8, month = 6, year = 2023),
            dateWise = true,
            repeatGapDuration = 14,
            postponeDuration = 20
        )
    )
}