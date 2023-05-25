package com.example.scheduler.firebase

import android.util.Log
import com.example.scheduler.data.Date
import com.example.scheduler.data.Task
import com.example.scheduler.data.Time
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object DatabaseFunctions {
    private val TAG = this::class.java.simpleName
    fun uploadTaskToFirebase(
        task: Task,
        onSuccessListener: () -> Unit,
        onFailureListener: (error: String) -> Unit
    ) {
        val data = hashMapOf(
            FirebaseConstants.task.title to task.title,
            FirebaseConstants.task.description to task.description,
            FirebaseConstants.task.timeForReminderHour to task.timeForReminder.hour,
            FirebaseConstants.task.timeForReminderMinute to task.timeForReminder.minute,
            FirebaseConstants.task.dateForReminderDay to task.dateForReminder.dayOfMonth,
            FirebaseConstants.task.dateForReminderMonth to task.dateForReminder.month,
            FirebaseConstants.task.dateForReminderYear to task.dateForReminder.year,
            FirebaseConstants.task.dateWise to task.dateWise,
            FirebaseConstants.task.repeatGapDuration to task.repeatGapDuration,
            FirebaseConstants.task.snoozeDuration to task.snoozeDuration,
            FirebaseConstants.task.postponeDuration to task.postponeDuration,
        )
        val database = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        database
            .collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .collection(FirebaseConstants.listOfTaskName)
            .document()
            .set(data)
            .addOnSuccessListener {
                onSuccessListener()
                Log.d(TAG, "added task to database")
            }.addOnFailureListener {
                onFailureListener(it.message ?: "error while adding task to remote database")
                it.printStackTrace()
            }
    }

    fun createUserDirectories(
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val h = hashMapOf(
            "userName" to email
        )
        db.collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .set(h)
            .addOnSuccessListener { Log.d(TAG, "success1");onSuccess() }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onFailure(e.message ?: "failed to create directories in the database")
            }
    }

    fun getListOfTasksFromDatastore(
        listReceiver: (List<Task>) -> Unit,
        onFailure: (issue: String) -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .collection(FirebaseConstants.listOfTaskName)
            .get()
            .addOnSuccessListener {
                val listOfTasks = mutableListOf<Task>()
                for (i in it.documents) {
                    listOfTasks.add(
                        Task(
                            title = i[FirebaseConstants.task.title].toString(),
                            description = i[FirebaseConstants.task.description].toString(),
                            timeForReminder = Time(
                                hour = i[FirebaseConstants.task.timeForReminderHour].toString()
                                    .toInt(),
                                minute = i[FirebaseConstants.task.timeForReminderMinute].toString()
                                    .toInt(),
                            ),
                            dateForReminder = Date(
                                dayOfMonth = i[FirebaseConstants.task.dateForReminderDay].toString()
                                    .toInt(),
                                month = i[FirebaseConstants.task.dateForReminderMonth].toString()
                                    .toInt(),
                                year = i[FirebaseConstants.task.dateForReminderYear].toString()
                                    .toInt(),
                            ),
                            dateWise = i[FirebaseConstants.task.dateWise].toString().toBoolean(),
                            repeatGapDuration = i[FirebaseConstants.task.repeatGapDuration].toString()
                                .toInt(),
                            snoozeDuration = i[FirebaseConstants.task.snoozeDuration].toString()
                                .toInt(),
                            postponeDuration = i[FirebaseConstants.task.postponeDuration].toString()
                                .toInt(),
                        )
                    )
                }
                listReceiver(listOfTasks)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onFailure(it.message ?: "error occurred while querying list of tasks")
            }
    }
}