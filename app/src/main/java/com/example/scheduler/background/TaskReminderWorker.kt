package com.example.scheduler.background

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

    companion object {
        const val channelID = "notification_id"

        const val dismissRequestCode = 2
        const val postponeRequestCode = 3

        const val dismissAction = "dismiss_notification"
        const val postponeAction = "postpone_notification"

        const val notificationTagKey = "notification_key"
        const val taskKey = "our_task_key"

        const val notificationId = 1
    }

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
    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: get notification permission
    } else {
        // TODO: add actions - dismiss, postpone
        //------------------------------------------------------------------------------------------
        val notificationId = Random.nextInt()
        val basicBroadcastIntent = Intent(context, SchedulerBroadcastReceiver::class.java).apply {
            val taskStr: String = Gson().toJson(task)
            putExtra(TaskReminderWorker.notificationTagKey, taskID)//adding values
            putExtra(TaskReminderWorker.taskKey, taskStr)//adding values
            putExtra(WorkerConstants.TaskWorker.notificationIdKey, notificationId)
        }
        val dismissPendingIntent =
            PendingIntent.getBroadcast(
                context,
                TaskReminderWorker.dismissRequestCode,
                basicBroadcastIntent.apply { action = TaskReminderWorker.dismissAction },
                Intent.FILL_IN_DATA or PendingIntent.FLAG_IMMUTABLE
            )
        val postponePendingIntent =
            PendingIntent.getBroadcast(
                context,
                TaskReminderWorker.postponeRequestCode,
                basicBroadcastIntent.apply { action = TaskReminderWorker.postponeAction },
                Intent.FILL_IN_DATA or PendingIntent.FLAG_IMMUTABLE
            )
        val notification =
            NotificationCompat
                .Builder(context, TaskReminderWorker.channelID)
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
            .notify(taskID, TaskReminderWorker.notificationId, notification)
    }
}

class SchedulerBroadcastReceiver : BroadcastReceiver() {
    val TAG = this::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        // TODO: fix: action on one notification runs code of another
        val docId = intent!!.getStringExtra(TaskReminderWorker.notificationTagKey)!!
        val task =
            Gson().fromJson(intent.getStringExtra(TaskReminderWorker.taskKey)!!, Task::class.java)

        when (intent.action) {
            TaskReminderWorker.dismissAction -> {
                Toast.makeText(
                    context, "task : [${task.title}] : completed", Toast.LENGTH_LONG
                ).show()
                NotificationManagerCompat
                    .from(context!!)
                    .cancel(docId, TaskReminderWorker.notificationId)
            }

            TaskReminderWorker.postponeAction -> {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).build()
                val oneTimeWorkRequest = OneTimeWorkRequest
                    .Builder(TaskReminderWorker::class.java)
                    .setInputData(CollectiveReminderWorker.getData(task, docId))
                    .setConstraints(constraints)
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .build()
                val workManager = WorkManager.getInstance(context!!)
                workManager.enqueueUniqueWork(
                    docId,
                    ExistingWorkPolicy.REPLACE,
                    oneTimeWorkRequest
                )
                Toast.makeText(context, "task : [${task.title}] : postponed", Toast.LENGTH_LONG)
                    .show()
            }

            else -> {
                // TODO:
            }
        }
    }
}