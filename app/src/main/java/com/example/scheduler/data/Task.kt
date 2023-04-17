package com.example.scheduler.data

data class Task(
    val title: String,
    val description: String,

    /** repeat in _____ days*/
    val repeatGapDuration: Int,

    /** time of day to remind about the task*/
    val remindAt: Int,

    /** when was the task set*/
    val start: Int,

    val delayTime: Int,
    val delayDuration: Int
)