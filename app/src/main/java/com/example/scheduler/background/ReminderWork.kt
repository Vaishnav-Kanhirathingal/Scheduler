package com.example.scheduler.background

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.scheduler.data.Task
import com.example.scheduler.firebase.DatabaseFunctions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

class ReminderWork(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    val TAG: String = this::class.java.simpleName

    override fun doWork(): Result {
        try {
            val workManager = WorkManager.getInstance(context)
            DatabaseFunctions.getListOfTasksAsDocuments(
                listReceiver = {
                    it.forEach { documentSnap ->
                        val task = Task.fromDocument(i = documentSnap)
                        if (task.isScheduledForToday()) {
                            // TODO: add timed reminder for each
                            try {
                                val timeRemaining = task.getTimeRemainingTillReminder()
                                val oneTimeWorkRequest = OneTimeWorkRequest
                                    .Builder(TaskReminderWorker::class.java)
                                    .setInputData(getData(task, documentSnap.id))
                                    .setInitialDelay(
                                        ((timeRemaining.hour * 60) + timeRemaining.minute).toLong(),
//                                    10,
                                        TimeUnit.MINUTES
//                                    TimeUnit.SECONDS
                                    )
                                    .build()
                                workManager.enqueueUniqueWork(
                                    documentSnap.id,
                                    ExistingWorkPolicy.REPLACE,
                                    oneTimeWorkRequest
                                )
                                Log.d(
                                    TAG, "added work for task: " +
                                            GsonBuilder().setPrettyPrinting().create().toJson(task)
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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
        fun getData(task: Task, id: String): Data {
            return Data
                .Builder()
                .putString(WorkerConstants.taskKey, Gson().toJson(task))
                .putString(WorkerConstants.documentIDKey, id)
                .build()
        }
    }
}

object WorkerConstants {
    const val taskKey = "task_key"
    const val documentIDKey = "document_id"
}