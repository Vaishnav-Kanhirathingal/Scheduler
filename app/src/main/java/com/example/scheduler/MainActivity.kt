package com.example.scheduler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scheduler.ui.destinations.AddTaskScreen
import com.example.scheduler.ui.destinations.DetailsScreen
import com.example.scheduler.ui.destinations.MainScreen
import com.example.scheduler.ui.screens.AddTaskScreen
import com.example.scheduler.ui.screens.MainScreen
import com.example.scheduler.ui.theme.SchedulerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController: NavHostController = rememberNavController()
                    SchedulerNavHost(
                        navController = navHostController,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

// TODO: add navigation
@Composable
fun SchedulerNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = MainScreen.route,
        modifier = modifier,
        builder = {
            composable(route = MainScreen.route) {
                MainScreen(
                    toAddTaskScreen = {
                        navController.navigate(AddTaskScreen.route)
                    }
                )
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