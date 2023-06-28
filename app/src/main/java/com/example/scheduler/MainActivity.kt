package com.example.scheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.scheduler.background.CollectiveReminderWorker
import com.example.scheduler.background.WorkerConstants
import com.example.scheduler.firebase.AccountFunctions
import com.example.scheduler.ui.destinations.Destinations
import com.example.scheduler.ui.screens.AddTaskScaffold
import com.example.scheduler.ui.screens.AppInfoScreen
import com.example.scheduler.ui.screens.SettingsScreen
import com.example.scheduler.ui.screens.SignUpScreen
import com.example.scheduler.ui.screens.main_screen.MainScreen
import com.example.scheduler.ui.theme.SchedulerTheme
import com.example.scheduler.values.PaddingCustomValues
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: remove replace work
        startWorker()
//        startTestWorker()
        auth = FirebaseAuth.getInstance()
        setContent {
            SchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    content = {
                        SchedulerNavHost(
                            modifier = Modifier,
                            googleSignInButton = { modifier: Modifier, onSuccess: () -> Unit, notifyUser: (issue: String) -> Unit ->
                                GoogleSignInButton(
                                    modifier = modifier,
                                    onSuccess = onSuccess,
                                    notifyUser = notifyUser
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    private fun startWorker() {
        createNotificationChannel()

        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        val initialDelay: Long = cal.timeInMillis - System.currentTimeMillis()

        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(CollectiveReminderWorker::class.java, 1L, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "reminder_initiator",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )

        Log.d(TAG, "Assigned work")
    }

    private fun startTestWorker() {
        createNotificationChannel()
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        WorkManager.getInstance(this)
            .enqueue(
                OneTimeWorkRequest
                    .Builder(CollectiveReminderWorker::class.java)
                    .setConstraints(constraints)
                    .build()
            )
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                WorkerConstants.channelID,
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
        notifyUser: (issue: String) -> Unit
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
                        notifyUser = notifyUser
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
    modifier: Modifier,
    googleSignInButton: @Composable (
        modifier: Modifier,
        onSuccess: () -> Unit,
        notifyUser: (issue: String) -> Unit,
    ) -> Unit
) {
    val navController = rememberNavController()
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
                        toSettingsPage = { navController.navigate(Destinations.SettingsScreen) },
                        toAppInfoScreen = { navController.navigate(Destinations.AppInfoScreen) },
                    )
                }
            )
            composable(
                route = Destinations.AddTaskScreen,
                content = { AddTaskScaffold(navigateUp = { navController.navigateUp() }) }
            )
            composable(
                route = Destinations.SettingsScreen,
                content = {
                    SettingsScreen(
                        navigateUp = { navController.navigateUp() },
                        navigateToSignUpScreen = { navController.navigate(Destinations.SignUpScreen) }
                    )
                }
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
            composable(
                route = Destinations.AppInfoScreen,
                content = { AppInfoScreen(back = { navController.navigateUp() }) }
            )
        }
    )
}