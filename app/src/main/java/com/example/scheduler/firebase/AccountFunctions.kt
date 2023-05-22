package com.example.scheduler.firebase

import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object AccountFunctions {
    private val TAG: String = this::class.java.simpleName
    private val auth = FirebaseAuth.getInstance()

    val signInLambda: (result: ActivityResult) -> Unit = { result: ActivityResult ->
        GoogleSignIn.getSignedInAccountFromIntent(result.data)
            .addOnSuccessListener {
                val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                auth
                    .signInWithCredential(credential)
                    .addOnSuccessListener {
                        val user = auth.currentUser
                        Log.d(TAG, "user = ${user?.email}")
                        DatabaseFunctions.createUserDirectories(
                            // TODO: correctly set the lambdas
                            onSuccess = {},
                            onFailure = {}
                        )
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }.addOnFailureListener {
                it.printStackTrace()
            }
    }
}