package com.example.scheduler.ui.destinations

interface Destinations {
    val route: String
    val displayName: String
}

object MainScreen : Destinations {
    override val route = "MainScreen"
    override val displayName = "Home"
}

object AddTaskScreen : Destinations {
    override val route = "AddTaskScreen"
    override val displayName = "Add New Task"
}

object DetailsScreen : Destinations {
    override val route = "DetailsScreen"
    override val displayName = "Task Details"
}