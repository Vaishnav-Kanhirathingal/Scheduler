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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.scheduler.R
import com.example.scheduler.data.StringFunctions
import com.example.scheduler.data.Task
import com.google.gson.Gson

class TaskReminderWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    companion object {
        const val channelID = "notification_id"

        const val dismissRequestCode = 2
        const val postponeRequestCode = 3

        const val dismissAction = "dismiss_notification"
        const val postponeAction = "postpone_notification"

        const val notificationTagKey = "notification_key"
        const val notificationId = 1
    }

    override fun doWork(): Result {
        return try {
            // TODO: get task
            val task =
                Gson().fromJson(inputData.getString(WorkerConstants.taskKey), Task::class.java)
            showNotification(
                task = task,
                context = context,
                taskID = inputData.getString(WorkerConstants.documentIDKey) ?: "error_getting_id"
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
        //------------------------------------------------------------------------------------------
        // TODO: add actions - dismiss, postpone, snooze
        val dismissIntent = Intent(context, SchedulerBroadcastReceiver::class.java).apply {
            action = TaskReminderWorker.dismissAction
            putExtra(TaskReminderWorker.notificationTagKey, taskID)
        }
        val dismissPendingIntent =
            PendingIntent.getBroadcast(
                context,
                TaskReminderWorker.dismissRequestCode,
                dismissIntent,
                Intent.FILL_IN_DATA or PendingIntent.FLAG_IMMUTABLE
            )

        //------------------------------------------------------------------------------------------
        val postponeIntent = Intent(context, SchedulerBroadcastReceiver::class.java).apply {
            action = TaskReminderWorker.postponeAction
            putExtra(TaskReminderWorker.notificationTagKey, taskID)
        }
        val postponePendingIntent =
            PendingIntent.getBroadcast(
                context,
                TaskReminderWorker.postponeRequestCode,
                postponeIntent,
                Intent.FILL_IN_DATA or PendingIntent.FLAG_IMMUTABLE
            )
        //------------------------------------------------------------------------------------------

        val notification = NotificationCompat
            .Builder(context, TaskReminderWorker.channelID)
            .setContentTitle(
                StringFunctions.getTimeAsText(
                    task.timeForReminder.hour,
                    task.timeForReminder.minute
                ) + ": " + task.title
            )
            .setContentText(task.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(task.description))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            TaskReminderWorker.dismissAction -> {
                val notificationTag = intent.getStringExtra(TaskReminderWorker.notificationTagKey)

                NotificationManagerCompat
                    .from(context!!)
                    .cancel(notificationTag, TaskReminderWorker.notificationId)

                Toast.makeText(context, "dismiss $notificationTag", Toast.LENGTH_LONG).show()
            }

            TaskReminderWorker.postponeAction -> {
                Toast.makeText(context, "postpone", Toast.LENGTH_LONG).show()
            }

            else -> {
                // TODO:
            }
        }

    }
}