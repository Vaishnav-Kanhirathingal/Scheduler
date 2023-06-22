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
import com.example.scheduler.MainActivity
import com.example.scheduler.R
import com.example.scheduler.data.StringFunctions
import com.example.scheduler.data.Task
import com.google.gson.Gson

class TaskReminderWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    companion object {
        const val channelID = "notification_id"
        const val activityRequestCode = 1

        const val dismissRequestCode = 2
        const val postponeRequestCode = 3

        const val dismissAction = "dismiss_notification"
        const val postponeAction = "postpone_notification"
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
    // TODO: add actions - dismiss, postpone, snooze
    //----------------------------------------------------------------------------------------------
    val actIntent = Intent(context, MainActivity::class.java)
    val actPending = PendingIntent.getActivity(context, 0, actIntent, PendingIntent.FLAG_IMMUTABLE)
    //----------------------------------------------------------------------------------------------
    val dismissPendingIntent = PendingIntent.getBroadcast(
        context,
        TaskReminderWorker.dismissRequestCode,
        Intent(
            context,
            SchedulerBroadcastReceiver::class.java
        ).apply { action = TaskReminderWorker.dismissAction },
        PendingIntent.FLAG_IMMUTABLE
    )
    //----------------------------------------------------------------------------------------------


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
//        .setOngoing(true)
        .addAction(R.drawable.task_24, "dismiss", dismissPendingIntent)
        .build()
    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: get notification permission
    } else {
        NotificationManagerCompat.from(context).notify(taskID, 1, notification)
    }
}

class SchedulerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            TaskReminderWorker.dismissAction -> {
                Toast.makeText(context, "notification button working", Toast.LENGTH_LONG).show()
            }
            else -> {
                // TODO:
            }
        }
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.cancel(notificationId)

    }
}