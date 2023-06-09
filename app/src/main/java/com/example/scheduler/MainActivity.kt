package com.example.scheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scheduler.background.ReminderWork
import com.example.scheduler.background.TaskReminderWorker
import com.example.scheduler.firebase.AccountFunctions
import com.example.scheduler.ui.destinations.Destinations
import com.example.scheduler.ui.screens.AddTaskScaffold
import com.example.scheduler.ui.screens.SettingsScreen
import com.example.scheduler.ui.screens.SignUpScreen
import com.example.scheduler.ui.screens.main_screen.MainScreen
import com.example.scheduler.ui.theme.SchedulerTheme
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
        IconButton(
            modifier = modifier,
            onClick = { startForResult.launch(googleSignInClient.signInIntent) },
            content = {
                val imageModifier = Modifier
                    .clip(CircleShape)
                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    Icon(
                        modifier = imageModifier,
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null
                    )
                } else {
                    AsyncImage(
                        modifier = imageModifier,
                        model = ImageRequest.Builder(this)
                            .data(user.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null
                    )
                }
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
        startDestination = Destinations.MainScreen,
        modifier = modifier,
        builder = {
            composable(
                route = Destinations.MainScreen,
                content = {
                    MainScreen(
                        toAddTaskScreen = { navController.navigate(Destinations.AddTaskScreen) },
                        googleSignInButton = googleSignInButton,
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
                content = { SignUpScreen() }
            )
        }
    )
}