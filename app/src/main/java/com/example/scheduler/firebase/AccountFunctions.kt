package com.example.scheduler.firebase

import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

/** contains all the firebase functions related to account activities
 */
object AccountFunctions {
    private val TAG: String = this::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()

    /** can be used to create a user and its own necessary directories
     * @param result this is the activity result of a one tap google sign up prompt
     * @param onSuccess a lambda to run after the function has executed successfully
     * @param notifyUser a lambda to run after the function has failed to execute. takes the
     * exception message as parameter
     */
    fun signInGoogle(
        result: ActivityResult,
        onSuccess: () -> Unit,
        notifyUser: (issue: String) -> Unit,
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(result.data)
            .addOnSuccessListener {
                val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        val user = auth.currentUser
                        Log.d(TAG, "user = ${user?.email}")
                        DatabaseFunctions.createUserDirectories(
                            onSuccess = onSuccess,
                            notifyUser = notifyUser
                        )
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        notifyUser(
                            if (e.message == null) {
                                "Failed to get sign in with given credentials"
                            } else {
                                "Credential login failed due to error: ${e.message!!}"
                            }
                        )
                    }
            }.addOnFailureListener {
                notifyUser(
                    if (it.message == null) {
                        "Failed to get signed in account from intent"
                    } else {
                        "Account login failed due to error: ${it.message!!}"
                    }
                )
                it.printStackTrace()
            }
    }

    /** deletes user account along with all its data
     * @param notifyUser a lambda which takes a string to be displayed to the user
     * @param onSuccess runs when the process is completed
     * @param dismissLoadingPrompt a lambda which dismisses the active loading animation prompt
     */
    fun deleteUserAccount(
        notifyUser: (String) -> Unit,
        onSuccess: () -> Unit,
        dismissLoadingPrompt: () -> Unit
    ) {
        Log.d(TAG, "started delete user")
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

        val deleteUserDocAndAcc = {
            userDocRef.delete().addOnSuccessListener {
                Log.d(TAG, "deleted user document")
                // TODO: fix: requires recent login
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

        listCollection.get().addOnSuccessListener {
            var counter = 0
            val size = it.documents.size
            if (size == 0) {
                deleteUserDocAndAcc()
            } else {
                it.documents.forEach { taskDS ->
                    listCollection.document(taskDS.id).delete().addOnSuccessListener {
                        counter++
                        if (counter == size) {
                            Log.d(TAG, "deleted task list")
                            deleteUserDocAndAcc()
                        }
                    }.addOnFailureListener(standardOnFailure)
                }
            }
        }.addOnFailureListener(standardOnFailure)
    }
}