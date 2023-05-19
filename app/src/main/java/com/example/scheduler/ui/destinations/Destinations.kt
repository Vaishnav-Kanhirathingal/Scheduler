package com.example.scheduler.ui.destinations

interface Destinations {
    val name: String
    val displayName: String
}

object MainScreen : Destinations {
    override val name = "MainScreen"
    override val displayName = "Home"
}

object AddTaskScreen : Destinations {
    override val name = "AddTaskScreen"
    override val displayName = "Add New Task"
}

object DetailsScreen:Destinations{
    override val name = "DetailsScreen"
    override val displayName = "Task Details"
}