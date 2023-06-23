package com.example.scheduler.background

object WorkerConstants {
    const val channelID = "notification_id"
    object CollectiveWorker {
        const val taskKey = "task_key"
        const val documentIDKey = "document_id"
    }

    object TaskWorker {
        const val notificationId = 1
        const val notificationTagKey = "notification_tag_key"
        const val taskKey = "our_task_key"

        object Action {
            const val dismiss = "dismiss"
            const val postpone = "postpone"
        }
    }
}