package com.example.scheduler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scheduler.ui.destinations.AddTaskScreen
import com.example.scheduler.ui.destinations.DetailsScreen
import com.example.scheduler.ui.destinations.MainScreen
import com.example.scheduler.ui.screens.AddTaskScreen
import com.example.scheduler.ui.screens.MainScreen
import com.example.scheduler.ui.theme.SchedulerTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInActivity: ActivityResultLauncher<Intent>

    val code = 48

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            SchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplaySignUpScreen()
                }
            }
        }
    }

    @Composable
    fun DisplaySignUpScreen() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)
        Button(
            onClick = {
//                googleSignInActivity.launch(client.signInIntent)
                signUp()
            },
            content = { Text(text = "sign in") }
        )

    }


    //----------------------------------------------------------------------------------------------
    fun signUp() {

        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnSuccessListener {
                firebaseAuthenticate(it.idToken)
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }
    }

    private fun firebaseAuthenticate(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnSuccessListener {
            val user = auth.currentUser
            Log.d(TAG, "user = ${user?.email}")
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    //----------------------compose sign up


    @Composable
    fun composableLogin() {
        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val startForResult =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        val intent = result.data
                        if (result.data != null) {
                            val task: Task<GoogleSignInAccount> =
                                GoogleSignIn.getSignedInAccountFromIntent(intent)
                            task.addOnSuccessListener {
                                Log.d("TAG", "success")
                            }.addOnFailureListener {
                                it.printStackTrace()
                            }
                        } else {
                            Log.d(TAG, "else section 2")
                        }
                    } else {
                        Log.d(TAG, "else section 1 result.resultCode = ${result.resultCode}")
                    }
                }
            )
        Button(
            onClick = { startForResult.launch(googleSignInClient.signInIntent) },
            content = { Text(text = "sign in") }
        )

    }

}

@Composable
fun SchedulerNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.route,
        modifier = modifier,
        builder = {
            composable(route = MainScreen.route) {
                MainScreen(toAddTaskScreen = { navController.navigate(AddTaskScreen.route) })
            }
            composable(route = AddTaskScreen.route) {
                // TODO: navigate up after adding the new task
                AddTaskScreen()
            }
            composable(route = DetailsScreen.route) {
                // TODO: add details screen
            }
        }
    )
}