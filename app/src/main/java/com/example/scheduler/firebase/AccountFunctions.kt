package com.example.scheduler.firebase

import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

object AccountFunctions {
    private val TAG: String = this::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()

    /** can be used to create a user and its own necessary directories
     * @param result this is the activity result of a one tap google sign up prompt
     * @param onSuccess a lambda to run after the function has executed successfully
     * @param onFailure a lambda to run after the function has failed to execute. takes the
     * exception message as parameter
     */
    fun signInGoogle(
        result: ActivityResult,
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit,
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(result.data)
            .addOnSuccessListener {
                val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                auth
                    .signInWithCredential(credential)
                    .addOnSuccessListener {
                        val user = auth.currentUser
                        Log.d(TAG, "user = ${user?.email}")
                        DatabaseFunctions.createUserDirectories(
                            onSuccess = onSuccess,
                            onFailure = onFailure
                        )
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        onFailure(e.message ?: "Failed to get sign in with given credentials")
                    }
            }.addOnFailureListener {
                onFailure(it.message ?: "Failed to get signed in account from intent")
                it.printStackTrace()
            }
    }

    fun deleteUserAccount(
        notifyUser: (String) -> Unit,
        onSuccess: () -> Unit,
        dismissLoadingPrompt: () -> Unit
    ) {
        val failureMessage = "failed to delete account"
        val user = FirebaseAuth.getInstance().currentUser!!
        val userDocRef = FirebaseFirestore
            .getInstance()
            .collection(FirebaseKeys.parentCollectionName)
            .document(user.email!!)
        val listCollection = userDocRef.collection(FirebaseKeys.listOfTaskName)
        val standardOnFailure = { e: Exception ->
            dismissLoadingPrompt()
            e.printStackTrace()
            notifyUser(failureMessage)
        }
        listCollection.get().addOnSuccessListener {
            var counter = 0
            val size = it.documents.size
            it.documents.forEach { taskDS ->
                listCollection.document(taskDS.id).delete().addOnSuccessListener {
                    counter++
                    if (counter == size) {
                        Log.d(TAG, "deleted task list")
                        userDocRef.delete().addOnSuccessListener {
                            Log.d(TAG, "deleted user document")
                            // TODO: requires recent login
                            user.delete().addOnSuccessListener {
                                Log.d(TAG, "deleted account")
                                dismissLoadingPrompt()
                                try {
                                    onSuccess()
                                } catch (e: Exception) {
                                    notifyUser("Failed to navigate to sign up screen")
                                    e.printStackTrace()
                                }
                            }.addOnFailureListener(standardOnFailure)
                        }.addOnFailureListener(standardOnFailure)
                    }
                }.addOnFailureListener(standardOnFailure)
            }
        }.addOnFailureListener(standardOnFailure)
    }
}