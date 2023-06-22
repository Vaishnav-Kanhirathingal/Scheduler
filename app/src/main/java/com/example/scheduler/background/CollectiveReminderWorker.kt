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

class CollectiveReminderWorker(private val context: Context, workerParameters: WorkerParameters) :
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
                            try {
                                val oneTimeWorkRequest = OneTimeWorkRequest
                                    .Builder(TaskReminderWorker::class.java)
                                    .setInputData(getData(task, documentSnap.id))
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
                        } else {
                            Log.d(TAG, "work not added for ${documentSnap.id}")
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