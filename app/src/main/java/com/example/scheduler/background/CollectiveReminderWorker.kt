package com.example.scheduler.background

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.scheduler.data.Task
import com.example.scheduler.firebase.DatabaseFunctions
import com.google.gson.Gson
import kotlin.random.Random

/** This worker class fetches all tasks from fire store and performs actions based on which task is
 * scheduled for the day.
 */
class CollectiveReminderWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private val TAG: String = this::class.java.simpleName

    override fun doWork(): Result {
        try {
            val workManager = WorkManager.getInstance(context)
            DatabaseFunctions.getListOfTasksAsDocuments(
                listReceiver = {
                    it.forEach { documentSnap ->
                        val task = Task.fromDocument(i = documentSnap)
                        if (task.isScheduledForToday()) {
                            TaskReminderWorker.scheduleWork(
                                task = task,
                                workManager = workManager,
                                documentId = documentSnap.id,
                                setPostponeDelay = false
                            )
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
            return Data
                .Builder()
                .putString(WorkerConstants.CollectiveWorker.taskKey, Gson().toJson(task))
                .putString(
                    WorkerConstants.CollectiveWorker.notificationTagKey,
                    Random.nextLong().toString()
                )
                .build()
        }
    }
}
