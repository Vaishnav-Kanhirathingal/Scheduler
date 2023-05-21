package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduler.data.Task
import com.example.scheduler.data.testTaskList
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues

@Composable
@Preview
fun MainScreenPreview() {
    MainScreen(toAddTaskScreen = { /*TODO*/ }) {
        IconButton(
            onClick = { },
            content = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null) },
//            modifier = Modifier.padding(horizontal = PaddingCustomValues.externalSpacing)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    toAddTaskScreen: () -> Unit,
    googleSignInButton: @Composable (modifier: Modifier) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val showFullText = remember { mutableStateOf(true) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { showFullText.value = (it == 0) }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Task List",
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    googleSignInButton(
                        Modifier
//                            .padding(horizontal = PaddingCustomValues.externalSpacing)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }, content = {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null,
//                            modifier = Modifier.padding(horizontal = PaddingCustomValues.externalSpacing)
                        )
                    }
                    )
                },
            )
        },
        floatingActionButton = {
            AddTaskFAB(
                showFullText = showFullText.value,
                toAddTaskScreen
            )
        },
        content = {
            SavedTaskList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                lazyListState = lazyListState
            )
        }
    )
}

@Composable
fun AddTaskFAB(showFullText: Boolean, toAddTaskScreen: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = {
            // TODO: move to "add tasks" page
            toAddTaskScreen()
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.Blue
            )
        },
        text = { Text(text = "Add Task") },
        expanded = showFullText,
    )

}

@Composable
fun SavedTaskList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        content = {
            items(
                count = testTaskList.size,
                itemContent = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingCustomValues.internalSpacing)
                    ) {
                        TaskCard(task = testTaskList[it])
                    }
                }
            )
        }
    )
}

@Composable
@Preview
fun TaskCardPreview() {
    TaskCard(task = testTaskList[3], modifier = Modifier.fillMaxWidth())
}

@Composable
fun TaskCard(task: Task, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        content = {
            Column(modifier = Modifier.padding(PaddingCustomValues.internalSpacing)) {
                Text(text = task.title, fontSize = FontSizeCustomValues.large)
                Text(text = task.description)
                Text(
                    text = "Reminder set for " +
                            getDateAsText(
                                y = task.dateForReminder.year,
                                m = task.dateForReminder.month,
                                d = task.dateForReminder.dayOfMonth
                            ) + " on " +
                            getTimeAsText(
                                hour = task.timeForReminder.hour,
                                minute = task.timeForReminder.minute
                            ) +
                            " and, " +
                            if (task.dateWise) {
                                "task is repeated on the ${numFormatter(task.dateForReminder.dayOfMonth)} of every month, "
                            } else {
                                if (task.repeatGapDuration == 0) {
                                    "task isn't repeated, "
                                } else {
                                    "task is repeated every ${task.repeatGapDuration} day${if (task.repeatGapDuration > 1) "s" else ""}, "
                                }
                            } + "snooze is available for ${task.snoozeDuration} minute" +
                            if (task.snoozeDuration > 1) {
                                "s "
                            } else {
                                " "
                            } +
                            "and, can be postponed by ${task.postponeDuration} day" + if (task.postponeDuration > 1) "s" else "",
                    // TODO: reduce line spacing
                )
            }
        }
    )
}