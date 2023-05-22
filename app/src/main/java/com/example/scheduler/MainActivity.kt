package com.example.scheduler

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scheduler.firebase.AccountFunctions
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.ui.destinations.AddTaskScreen
import com.example.scheduler.ui.destinations.DetailsScreen
import com.example.scheduler.ui.destinations.MainScreen
import com.example.scheduler.ui.screens.AddTaskScreen
import com.example.scheduler.ui.screens.MainScreen
import com.example.scheduler.ui.theme.SchedulerTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            SchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController: NavHostController = rememberNavController()
                    SchedulerNavHost(
                        navController = navHostController,
                        modifier = Modifier,
                        googleSignInButton = { GoogleSignInButton(it) }
                    )
                }
            }
        }
    }

    @Composable
    fun GoogleSignInButton(modifier: Modifier) {

        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val startForResult =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {
                    AccountFunctions.signInGoogle(
                        // TODO: correctly set lambda parameters
                        result = it,
                        onSuccess = {},
                        onFailure = {}
                    )
                }
            )
        IconButton(
            modifier = modifier,
            onClick = { startForResult.launch(googleSignInClient.signInIntent) },
            content = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null) }
        )
        DatabaseFunctions.getListOfTasksFromDatastore {
            Log.d(
                DatabaseFunctions.TAG, "list received = " +
                        GsonBuilder().setPrettyPrinting().create().toJson(it)
            )
        }
    }
}

@Composable
fun SchedulerNavHost(
    navController: NavHostController,
    modifier: Modifier,
    googleSignInButton: @Composable (modifier: Modifier) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.route,
        modifier = modifier,
        builder = {
            composable(route = MainScreen.route) {
                MainScreen(
                    toAddTaskScreen = { navController.navigate(AddTaskScreen.route) },
                    googleSignInButton = googleSignInButton
                )
            }
            composable(route = AddTaskScreen.route) {
                // TODO: navigate up after adding the new task
                AddTaskScreen {
                    // TODO: navigate up after completion and save the task
                }
            }
            composable(route = DetailsScreen.route) {
                // TODO: add details screen
            }
        }
    )
}
