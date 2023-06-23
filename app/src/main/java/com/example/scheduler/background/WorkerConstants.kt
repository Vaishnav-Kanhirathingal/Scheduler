package com.example.scheduler.background

/** this class contains majority of the constants required by the package.
 */
object WorkerConstants {
    /** notification channel Id
     */
    const val channelID = "notification_id"

    /** contains data keys
     */
    object CollectiveWorker {
        const val taskKey = "task_key"
        const val documentIDKey = "document_id"
    }

    /** contains data keys, the constant notificationID and action keys
     */
    object TaskWorker {
        const val notificationId = 1
        const val notificationTagKey = "notification_tag_key"
        const val taskKey = "our_task_key"

        /** contains action keys
         */
        object Action {
            const val dismiss = "dismiss"
            const val postpone = "postpone"
        }
    }
}