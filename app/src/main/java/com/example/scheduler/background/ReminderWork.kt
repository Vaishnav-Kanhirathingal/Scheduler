package com.example.scheduler.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWork(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}