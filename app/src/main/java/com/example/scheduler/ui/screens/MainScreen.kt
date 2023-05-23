package com.example.scheduler.ui.screens

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.twotone.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduler.data.Repetitions
import com.example.scheduler.data.Reps
import com.example.scheduler.data.StringFunctions.getDateAsText
import com.example.scheduler.data.StringFunctions.getTextWithS
import com.example.scheduler.data.StringFunctions.getTimeAsText
import com.example.scheduler.data.StringFunctions.numFormatter
import com.example.scheduler.data.Task
import com.example.scheduler.data.testTaskList
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    toAddTaskScreen: () -> Unit,
    googleSignInButton: @Composable (
        modifier: Modifier,
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit,
    ) -> Unit
) {
    val snackBarHostState = SnackbarHostState()
    val lazyListState = rememberLazyListState()
    val showFullText = remember { mutableStateOf(true) }

    val filter = remember { mutableStateOf(Repetitions.ALL) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { showFullText.value = (it == 0) }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
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
                        Modifier,
                        {
                            CoroutineScope(Dispatchers.IO).launch {
                                snackBarHostState.showSnackbar(
                                    message = "Login Successful",
                                    withDismissAction = true
                                )
                            }
                        },
                        {
                            CoroutineScope(Dispatchers.IO).launch {
                                snackBarHostState.showSnackbar(
                                    message = "Login Unsuccessful, reason = $it",
                                    withDismissAction = true
                                )
                            }
                        }
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
                toAddTaskScreen = toAddTaskScreen
            )
        },
        content = {
            val receivedList = remember { mutableStateListOf<Task>() }
            Column {
                DatabaseFunctions.getListOfTasksFromDatastore(
                    listReceiver = {
                        receivedList.clear()
                        for (i in it) {
                            val ret = receivedList.add(i)
                            Log.d(
                                TAG, "added: $ret = " +
                                        GsonBuilder().setPrettyPrinting().create().toJson(i)
                            )
                        }
                    },
                    onFailure = {
                        CoroutineScope(Dispatchers.IO).launch {
                            snackBarHostState.showSnackbar(
                                message = it,
                                withDismissAction = true,
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                )
                FilterRow(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxWidth(),
                    filterSelected = filter
                )
                Button(
                    onClick = {
                        for (i in testTaskList) {
                            Log.d(TAG, i.nextReminderIsScheduledFor().toString())
                        }
                    },
                    content = {
                        Text(text = "Test")
                    }
                )
                SavedTaskList(
                    modifier = Modifier.fillMaxWidth(),
                    lazyListState = lazyListState,
                    listOfTaskReceived = receivedList,
                    selected = filter
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FilterRowPreview() {
    FilterRow(
        filterSelected = remember {
            mutableStateOf(Repetitions.DAY)
        }
    )
}

@Composable
fun FilterRow(modifier: Modifier = Modifier, filterSelected: MutableState<Reps>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingCustomValues.internalSpacing),
        modifier = modifier
            .padding(horizontal = PaddingCustomValues.internalSpacing)
            .fillMaxWidth()
            .horizontalScroll(ScrollState(0)),
        content = {
            TimeFilterChip(
                modifier = Modifier.weight(1f),
                currentChoice = filterSelected,
                chipFilterType = Repetitions.ALL
            )
            TimeFilterChip(
                modifier = Modifier.weight(1f),
                currentChoice = filterSelected,
                chipFilterType = Repetitions.DAY
            )
            TimeFilterChip(
                modifier = Modifier.weight(1f),
                currentChoice = filterSelected,
                chipFilterType = Repetitions.WEEK
            )
            TimeFilterChip(
                modifier = Modifier.weight(1f),
                currentChoice = filterSelected,
                chipFilterType = Repetitions.MONTH
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterChip(
    modifier: Modifier = Modifier,
    currentChoice: MutableState<Reps>,
    chipFilterType: Reps
) {
    FilterChip(
        modifier = modifier,
        selected = currentChoice.value.enumValue == chipFilterType.enumValue,
        onClick = { currentChoice.value = chipFilterType },
        label = {
            Text(
                text = chipFilterType.timeUnit,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
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
@Preview(showBackground = true)
fun SavedTaskListPreview() {
    val receivedList = remember { mutableStateListOf<Task>() }
    receivedList.addAll(testTaskList)
    SavedTaskList(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        lazyListState = LazyListState(0, 0),
        listOfTaskReceived = receivedList,
        selected = remember { mutableStateOf(Repetitions.MONTH) }
    )
}

@Composable
fun SavedTaskList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    listOfTaskReceived: SnapshotStateList<Task>,
    selected: MutableState<Reps>
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        content = {
            items(
                count = listOfTaskReceived.size,
                itemContent = {
                    val task = listOfTaskReceived[it]
                    if (task.isScheduledIn(selected.value.step) || selected.value == Repetitions.ALL) {
                        // TODO: removve card
                        DetailedTaskCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PaddingCustomValues.internalSpacing),
                            task = task,
                            selected = selected
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun DetailedTaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    selected: MutableState<Reps>
) {
    // TODO: add a UI element that tells how much time remaining till the next alarm
    Card(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.padding(PaddingCustomValues.externalSpacing),
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                // TODO: this icon should denote whether the task is scheduled for today or not
                                imageVector =
                                when {
                                    task.isScheduledIn(Repetitions.DAY.step) -> Icons.Outlined.Build
                                    task.isScheduledIn(Repetitions.WEEK.step) -> Icons.TwoTone.Build
                                    task.isScheduledIn(Repetitions.MONTH.step) -> Icons.Filled.Build
                                    else -> Icons.Outlined.Warning
                                }
//                                if (task.isScheduled(selected.value.step)) {
//                                    Icons.Filled.Build
//                                } else {
//                                    Icons.Outlined.Build
//                                }
                                ,
                                contentDescription = null
                            )
                            Text(
                                text = task.title,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = PaddingCustomValues.externalSpacing),
                                fontSize = FontSizeCustomValues.large
                            )
                            IconButton(
                                onClick = { TODO("move to edit page for the selected task") },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = null
                                    )
                                }
                            )
                            IconButton(
                                onClick = { TODO("delete selected task") },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    )
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color(0, 0, 0)
                    )
                    DetailsRow(text = task.description, icon = Icons.Outlined.Info)
                    DetailsRow(
                        text = getTimeAsText(
                            hour = task.timeForReminder.hour,
                            minute = task.timeForReminder.minute
                        ) + " on " +
                                getDateAsText(
                                    y = task.dateForReminder.year,
                                    m = task.dateForReminder.month,
                                    d = task.dateForReminder.dayOfMonth
                                ),
                        icon = Icons.Outlined.DateRange
                    )

                    DetailsRow(
                        text = if (task.dateWise) {
                            "Repeated on the ${numFormatter(task.dateForReminder.dayOfMonth)} of every month"
                        } else if (task.repeatGapDuration == 0) {
                            "Not repeated"
                        } else {
                            "Repeated every ${getTextWithS("day", task.repeatGapDuration)}"
                        },
                        icon = Icons.Outlined.Refresh
                    )
                    DetailsRow(
                        text = getTextWithS(unit = "minute", num = task.snoozeDuration)
                                + " or " +
                                getTextWithS(unit = "day", num = task.postponeDuration),
                        icon = Icons.Outlined.ArrowForward
                    )
                }
            )
        }
    )
}

@Composable
fun DetailsRow(text: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .padding(top = PaddingCustomValues.internalSpacing)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = PaddingCustomValues.internalSpacing)
            )
        }
    )

}