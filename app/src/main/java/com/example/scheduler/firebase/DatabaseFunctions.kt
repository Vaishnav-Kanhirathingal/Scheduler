package com.example.scheduler.firebase

import android.util.Log
import com.example.scheduler.data.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object DatabaseFunctions {
    val TAG = this::class.java.simpleName
    fun uploadTaskToFirebase(
        task: Task,
        onSuccessListener: () -> Unit,
        onFailureListener: () -> Unit
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
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val h = hashMapOf(
            "userName" to email
        )
        db
            .collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .collection(FirebaseConstants.listOfTaskName)
            .document()
            .set(data)
            .addOnSuccessListener {
                onSuccessListener()
                Log.d(TAG, "added task to database")
            }.addOnFailureListener {
                onFailureListener()
                it.printStackTrace()
            }
    }

    fun createUserDirectories(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email!!
        val h = hashMapOf(
            "userName" to email
        )
        db.collection(FirebaseConstants.parentCollectionName)
            .document(email)
            .set(h)
            .addOnSuccessListener {
                Log.d(TAG, "success1")
            }.addOnFailureListener { e -> e.printStackTrace() }
    }
}