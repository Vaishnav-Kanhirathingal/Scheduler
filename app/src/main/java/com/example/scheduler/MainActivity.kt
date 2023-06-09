package com.example.scheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.scheduler.background.ReminderWork
import com.example.scheduler.background.TaskReminderWorker
import com.example.scheduler.firebase.AccountFunctions
import com.example.scheduler.ui.destinations.Destinations
import com.example.scheduler.ui.screens.AddTaskScaffold
import com.example.scheduler.ui.screens.SettingsScreen
import com.example.scheduler.ui.screens.SignUpScreen
import com.example.scheduler.ui.screens.main_screen.MainScreen
import com.example.scheduler.ui.theme.SchedulerTheme
import com.example.scheduler.values.PaddingCustomValues
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(OneTimeWorkRequest.from(ReminderWork::class.java))

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
                        googleSignInButton = { modifier: Modifier, onSuccess: () -> Unit, onFailure: (issue: String) -> Unit ->
                            GoogleSignInButton(
                                modifier = modifier,
                                onSuccess = onSuccess,
                                onFailure = onFailure
                            )
                        }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                TaskReminderWorker.channelID,
                "name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        val manager: NotificationManager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    @Composable
    fun GoogleSignInButton(
        modifier: Modifier,
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit
    ) {

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
                        result = it,
                        onSuccess = onSuccess,
                        onFailure = onFailure
                    )
                }
            )
        ElevatedButton(
            modifier = modifier,
            onClick = { startForResult.launch(googleSignInClient.signInIntent) },
            content = {
                Icon(
                    modifier = Modifier.padding(all = PaddingCustomValues.mediumSpacing),
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = null
                )
                Text(text = "Google Sign In")
            }
        )
    }
}

@Composable
fun SchedulerNavHost(
    navController: NavHostController,
    modifier: Modifier,
    googleSignInButton: @Composable (
        modifier: Modifier,
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit,
    ) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser == null) {
            Destinations.SignUpScreen
        } else {
            Destinations.MainScreen
        },
        modifier = modifier,
        builder = {
            composable(
                route = Destinations.MainScreen,
                content = {
                    MainScreen(
                        toAddTaskScreen = { navController.navigate(Destinations.AddTaskScreen) },
                        toSettingsPage = { navController.navigate(Destinations.SettingsScreen) }
                    )
                }
            )
            composable(
                route = Destinations.AddTaskScreen,
                content = { AddTaskScaffold(navigateUp = { navController.navigateUp() }) }
            )
            composable(
                route = Destinations.SettingsScreen,
                content = { SettingsScreen(navigateUp = { navController.navigateUp() }) }
            )
            composable(
                route = Destinations.SignUpScreen,
                content = {
                    SignUpScreen(
                        googleSignInButton = googleSignInButton,
                        navigateToMainScreen = {
                            // TODO: make this on top
                            navController.navigate(Destinations.MainScreen)
                        }
                    )
                }
            )
        }
    )
}