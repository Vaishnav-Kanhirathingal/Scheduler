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
import com.example.scheduler.data.Task

class TaskReminderWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    companion object {
        const val channelID = "notification_id"
    }

    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}

fun showNotification(task: Task, context: Context, taskID: String) {
    val notification = NotificationCompat
        .Builder(context, TaskReminderWorker.channelID)
        .setContentTitle(task.title)
        .setContentText(task.description)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
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