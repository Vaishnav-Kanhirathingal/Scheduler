package com.example.scheduler.background

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.scheduler.data.Task
import com.example.scheduler.firebase.DatabaseFunctions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.random.Random

/** This worker class fetches all tasks from fire store and performs actions based on which task is
 * scheduled for the day.
 */
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
                                val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED).build()
                                val oneTimeWorkRequest = OneTimeWorkRequest
                                    .Builder(TaskReminderWorker::class.java)
                                    .setInputData(getData(task = task))
                                    .setConstraints(constraints)
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
        /** creates data for intent using the parameter given.
         * @param task gets embedded into data with key [WorkerConstants.CollectiveWorker.taskKey]
         * along with a notificationTag with key [WorkerConstants.CollectiveWorker.notificationTagKey]
         */
        fun getData(task: Task): Data {
            val notificationTag = Random.nextLong().toString()
            return Data
                .Builder()
                .putString(WorkerConstants.CollectiveWorker.taskKey, Gson().toJson(task))
                .putString(WorkerConstants.CollectiveWorker.notificationTagKey, notificationTag)
                .build()
        }
    }
}
