package com.example.scheduler.background

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.scheduler.data.Task
import com.example.scheduler.firebase.DatabaseFunctions
import com.google.gson.Gson

class ReminderWork(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    val TAG = this::class.java.simpleName
    override fun doWork(): Result {
        try {
            val workManager = WorkManager.getInstance(context)
            DatabaseFunctions.getListOfTasksAsDocuments(
                listReceiver = {
                    it.forEach { documentSnap ->
                        val task = Task.fromDocument(i = documentSnap)
                        if (task.isScheduledForToday()) {
                            // TODO: add reminder for each
                            workManager.enqueue(
                                OneTimeWorkRequestBuilder<TaskReminderWorker>()
                                    .setInputData(getData(task))
                                    .build()
                            )
                        }
                    }
                },
                onFailure = {}
            )
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    companion object {
        fun getData(task: Task): Data {
            return Data.Builder().putString(WorkerConstants.taskKey, Gson().toJson(task)).build()
        }
    }
}

object WorkerConstants {
    const val taskKey = "task_key"
}