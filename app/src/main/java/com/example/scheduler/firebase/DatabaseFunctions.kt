package com.example.scheduler.firebase

import android.util.Log
import com.example.scheduler.data.Date
import com.example.scheduler.data.Task
import com.example.scheduler.data.Time
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object DatabaseFunctions {
    private val TAG = this::class.java.simpleName
    fun uploadTaskToFirebase(
        task: Task,
        onSuccessListener: () -> Unit,
        onFailureListener: (error: String) -> Unit
    ) {
        val data = hashMapOf(
            FirebaseConstants.TaskName.title to task.title,
            FirebaseConstants.TaskName.description to task.description,
            FirebaseConstants.TaskName.timeForReminderHour to task.timeForReminder.hour,
            FirebaseConstants.TaskName.timeForReminderMinute to task.timeForReminder.minute,
            FirebaseConstants.TaskName.dateForReminderDay to task.dateForReminder.dayOfMonth,
            FirebaseConstants.TaskName.dateForReminderMonth to task.dateForReminder.month,
            FirebaseConstants.TaskName.dateForReminderYear to task.dateForReminder.year,
            FirebaseConstants.TaskName.dateWise to task.dateWise,
            FirebaseConstants.TaskName.repeatGapDuration to task.repeatGapDuration,
            FirebaseConstants.TaskName.snoozeDuration to task.snoozeDuration,
            FirebaseConstants.TaskName.postponeDuration to task.postponeDuration,
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

    fun getListOfTasksAsDocuments(
        listReceiver: (List<DocumentSnapshot>) -> Unit,
        onFailure: (issue: String) -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val db = FirebaseFirestore.getInstance()

        db.collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .collection(FirebaseConstants.listOfTaskName)
            .get()
            .addOnSuccessListener {
                listReceiver(it.documents)
            }.addOnFailureListener {
                it.printStackTrace()
                onFailure(it.message ?: "error occurred while querying list of tasks")
            }
    }

    fun getTaskFromDocument(i: DocumentSnapshot): Task {
        return Task(
            title = i[FirebaseConstants.TaskName.title].toString(),
            description = i[FirebaseConstants.TaskName.description].toString(),
            timeForReminder = Time(
                hour = i[FirebaseConstants.TaskName.timeForReminderHour].toString().toInt(),
                minute = i[FirebaseConstants.TaskName.timeForReminderMinute].toString().toInt(),
            ),
            dateForReminder = Date(
                dayOfMonth = i[FirebaseConstants.TaskName.dateForReminderDay].toString().toInt(),
                month = i[FirebaseConstants.TaskName.dateForReminderMonth].toString().toInt(),
                year = i[FirebaseConstants.TaskName.dateForReminderYear].toString().toInt(),
            ),
            dateWise = i[FirebaseConstants.TaskName.dateWise].toString().toBoolean(),
            repeatGapDuration = i[FirebaseConstants.TaskName.repeatGapDuration].toString().toInt(),
            snoozeDuration = i[FirebaseConstants.TaskName.snoozeDuration].toString().toInt(),
            postponeDuration = i[FirebaseConstants.TaskName.postponeDuration].toString().toInt(),
        )
    }

    fun deleteTaskDocument(
        taskDoc: DocumentSnapshot,
        onSuccessListener: () -> Unit,
        onFailureListener: (error: String) -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .collection(FirebaseConstants.listOfTaskName)
            .document(taskDoc.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "deleted task")
                onSuccessListener()
            }.addOnFailureListener {
                it.printStackTrace()
                onFailureListener(it.message ?: "deleteTaskDocument error")
            }
    }
}