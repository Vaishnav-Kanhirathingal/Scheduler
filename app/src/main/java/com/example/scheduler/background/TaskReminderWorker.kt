package com.example.scheduler.background

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
        const val snoozeRequestCode = 1
        const val postponeRequestCode = 2
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
        .build()

    if (
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing
        //  permissions, and then overriding public void onRequestPermissionsResult(int requestCode,
        //  String[] permissions, int[] grantResults) to handle the case where the user grants the
        //  permission. See the documentation for ActivityCompat#requestPermissions for more details.
    } else {
        NotificationManagerCompat.from(context).notify(taskID, 1, notification)
    }
}

