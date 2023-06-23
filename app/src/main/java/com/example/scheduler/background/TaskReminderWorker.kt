package com.example.scheduler.background

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.scheduler.R
import com.example.scheduler.data.StringFunctions
import com.example.scheduler.data.Task
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TaskReminderWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    val TAG = this::class.java.simpleName

    override fun doWork(): Result {
        return try {
            // TODO: get task
            val task =
                Gson().fromJson(
                    inputData.getString(WorkerConstants.CollectiveWorker.taskKey),
                    Task::class.java
                )
            showNotification(
                task = task,
                context = context,
                taskID = inputData.getString(WorkerConstants.CollectiveWorker.documentIDKey)
                    ?: "error_getting_id"
            )
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}

fun showNotification(task: Task, context: Context, taskID: String) {
    val TAG = "showNotification"
    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: get notification permission
    } else {
        val basicBroadcastIntent = Intent(context, SchedulerBroadcastReceiver::class.java).apply {
            val taskStr: String = Gson().toJson(task)
            putExtra(WorkerConstants.TaskWorker.notificationTagKey, taskID)//adding values
            putExtra(WorkerConstants.TaskWorker.taskKey, taskStr)//adding values
        }
        val dismissPendingIntent =
            PendingIntent.getBroadcast(
                context,
                Random.nextInt(),
                basicBroadcastIntent.apply { action = WorkerConstants.TaskWorker.Action.dismiss },
                Intent.FILL_IN_DATA or PendingIntent.FLAG_IMMUTABLE
            )
        val postponePendingIntent =
            PendingIntent.getBroadcast(
                context,
                Random.nextInt(),
                basicBroadcastIntent.apply { action = WorkerConstants.TaskWorker.Action.postpone },
                Intent.FILL_IN_DATA or PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat
                .Builder(context, WorkerConstants.channelID)
                .setContentTitle(
                    StringFunctions.getTimeAsText(
                        task.timeForReminder.hour, task.timeForReminder.minute
                    ) + ": " + task.title
                )
                .setContentText(task.description)
                .setStyle(NotificationCompat.BigTextStyle().bigText(task.description))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .addAction(R.drawable.task_24, "dismiss", dismissPendingIntent)
                .addAction(
                    R.drawable.skip_next_24,
                    "postpone ${StringFunctions.getTextWithS("day", task.postponeDuration)}",
                    postponePendingIntent
                )
                .build()
        NotificationManagerCompat.from(context)
            .notify(taskID, WorkerConstants.TaskWorker.notificationId, notification)
    }
}

/** this class is a BroadcastReceiver which is responsible for handling actions performed on
 * notifications produced bby TaskWorker.
 */
class SchedulerBroadcastReceiver : BroadcastReceiver() {
    private val TAG = this::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        // TODO: check if initial delay works properly
        val docId = intent!!.getStringExtra(WorkerConstants.TaskWorker.notificationTagKey)!!
        val task =
            Gson().fromJson(
                intent.getStringExtra(WorkerConstants.TaskWorker.taskKey)!!,
                Task::class.java
            )

        NotificationManagerCompat.from(context!!)
            .cancel(docId, WorkerConstants.TaskWorker.notificationId)

        when (intent.action) {
            WorkerConstants.TaskWorker.Action.dismiss -> {
                Toast.makeText(
                    context, "task : [${task.title}] : completed", Toast.LENGTH_LONG
                ).show()
            }

            WorkerConstants.TaskWorker.Action.postpone -> {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).build()
                val oneTimeWorkRequest = OneTimeWorkRequest
                    .Builder(TaskReminderWorker::class.java)
                    .setInputData(CollectiveReminderWorker.getData(task, docId))
                    .setConstraints(constraints)
                    .setInitialDelay(task.timeAfterDelayMillis(), TimeUnit.MILLISECONDS)
                    .build()
                val workManager = WorkManager.getInstance(context)
                workManager.enqueueUniqueWork(
                    docId,
                    ExistingWorkPolicy.REPLACE,
                    oneTimeWorkRequest
                )
                Toast.makeText(context, "task : [${task.title}] : postponed", Toast.LENGTH_LONG)
                    .show()
            }

            else -> Log.d(TAG, "action button error")
        }
    }
}