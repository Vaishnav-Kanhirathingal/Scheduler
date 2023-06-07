package com.example.scheduler.firebase

import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object AccountFunctions {
    private val TAG: String = this::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()

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
}