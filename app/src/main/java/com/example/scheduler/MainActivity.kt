package com.example.scheduler

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
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

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    val navHostController: NavHostController = rememberNavController()
//                    SchedulerNavHost(
//                        navController = navHostController,
//                        modifier = Modifier
//                    )
                    DisplaySignUpScreen()
                }
            }
        }
    }

    @Composable
    fun DisplaySignUpScreen() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.client_id))
            .requestId()
            .requestProfile()
            .build()
        val client = GoogleSignIn.getClient(this, gso)
        val startForResult =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result: ActivityResult ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val intent = result.data
                        if (result.data != null) {
                            val task: Task<GoogleSignInAccount> =
                                GoogleSignIn.getSignedInAccountFromIntent(intent)
                            task.addOnSuccessListener {
                                Log.d("TAG", "success")
                            }.addOnFailureListener {
                                it.printStackTrace()
                            }
                        }else{
                            Log.d(TAG,"else section 2")
                        }
                    }else{
                        Log.d(TAG,"else section 1 result.resultCode = ${result.resultCode}")
                    }
                }
            )
        Button(
            onClick = { startForResult.launch(client.signInIntent) },
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
