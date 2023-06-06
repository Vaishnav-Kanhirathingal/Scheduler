package com.example.scheduler.firebase

import android.util.Log
import com.example.scheduler.data.Task
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
            FirebaseKeys.TaskName.title to task.title,
            FirebaseKeys.TaskName.description to task.description,
            FirebaseKeys.TaskName.timeForReminderHour to task.timeForReminder.hour,
            FirebaseKeys.TaskName.timeForReminderMinute to task.timeForReminder.minute,
            FirebaseKeys.TaskName.dateForReminderDay to task.dateForReminder.dayOfMonth,
            FirebaseKeys.TaskName.dateForReminderMonth to task.dateForReminder.month,
            FirebaseKeys.TaskName.dateForReminderYear to task.dateForReminder.year,
            FirebaseKeys.TaskName.dateWise to task.dateWise,
            FirebaseKeys.TaskName.repeatGapDuration to task.repeatGapDuration,
            FirebaseKeys.TaskName.snoozeDuration to task.snoozeDuration,
            FirebaseKeys.TaskName.postponeDuration to task.postponeDuration,
        )
        val database = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        database
            .collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)
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
        db.collection(FirebaseKeys.parentCollectionName)
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

        db.collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)
            .get()
            .addOnSuccessListener {
                listReceiver(it.documents)
            }.addOnFailureListener {
                it.printStackTrace()
                onFailure(it.message ?: "error occurred while querying list of tasks")
            }
    }

    fun deleteTaskDocument(
        taskDoc: DocumentSnapshot,
        onSuccessListener: () -> Unit,
        onFailureListener: (error: String) -> Unit
    ) {
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val db = FirebaseFirestore.getInstance()
        db.collection(FirebaseKeys.parentCollectionName)
            .document(email)
            .collection(FirebaseKeys.listOfTaskName)
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