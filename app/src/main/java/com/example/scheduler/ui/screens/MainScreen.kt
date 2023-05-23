package com.example.scheduler.ui.screens

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

private const val TAG = "MainScreen"

@Composable
@Preview
fun MainScreenPreview() {
    MainScreen(toAddTaskScreen = { /*TODO*/ }) {
        IconButton(
            onClick = { },
            content = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null) }
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
                toAddTaskScreen = toAddTaskScreen
            )
        },
        content = {
            // TODO: save received value as a savable state list
            val receivedList = remember { mutableStateListOf<Task>() }
            Column {
                DatabaseFunctions.getListOfTasksFromDatastore(
                    // TODO: correctly set [onSuccess] and [onFailure] parameters
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
                    onSuccess = {},
                    onFailure = {}
                )

                FilterRow(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxWidth()
                )
                SavedTaskList(
                    modifier = Modifier.fillMaxWidth(),
                    lazyListState = lazyListState,
                    listOfTaskReceived = receivedList
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun FilterRow(modifier: Modifier = Modifier) {
    val filterSelected = remember { mutableStateOf(TimeFilter.DAY) }
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
                text = "Day",
                chipFilterType = TimeFilter.DAY
            )
            TimeFilterChip(
                modifier = Modifier.weight(1f),
                currentChoice = filterSelected,
                text = "Week",
                chipFilterType = TimeFilter.WEEK
            )
            TimeFilterChip(
                modifier = Modifier.weight(1f),
                currentChoice = filterSelected,
                text = "Month",
                chipFilterType = TimeFilter.MONTH
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterChip(
    modifier: Modifier = Modifier,
    currentChoice: MutableState<TimeFilter>,
    text: String,
    chipFilterType: TimeFilter
) {
    FilterChip(
        modifier = modifier,
        selected = currentChoice.value == chipFilterType,
        onClick = { currentChoice.value = chipFilterType },
        label = {
            Text(
                text = text,
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
fun SavedTaskList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    listOfTaskReceived: SnapshotStateList<Task>
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        content = {
            items(
                count = listOfTaskReceived.size,
                itemContent = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingCustomValues.internalSpacing)
                    ) {
//                        TaskCard(task = testTaskList[it])
                        DetailedTaskCard(task = listOfTaskReceived[it])
                    }
                }
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
fun DetailedTaskCardComparePreview() {
    Column(
        modifier = Modifier.padding(PaddingCustomValues.internalSpacing),
        content = {
            DetailedTaskCard(
                task = testTaskList[1], modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingCustomValues.internalSpacing)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingCustomValues.internalSpacing),
                content = {
                    Text(
                        text = GsonBuilder().setPrettyPrinting().create().toJson(testTaskList[1]),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingCustomValues.externalSpacing)
                    )
                }
            )
        }
    )
}

@Composable
fun DetailedTaskCard(task: Task, modifier: Modifier = Modifier) {
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
                                imageVector = Icons.Outlined.Build,// TODO: this icon should denote whether the task is scheduled for today or not
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
                    Row(
                        modifier = Modifier
                            .padding(top = PaddingCustomValues.internalSpacing)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                            Text(
                                text = task.description,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = PaddingCustomValues.internalSpacing)
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = PaddingCustomValues.internalSpacing)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null)
                            Text(
                                text = getTimeAsText(
                                    hour = task.timeForReminder.hour,
                                    minute = task.timeForReminder.minute
                                ) + " on " +
                                        getDateAsText(
                                            y = task.dateForReminder.year,
                                            m = task.dateForReminder.month,
                                            d = task.dateForReminder.dayOfMonth
                                        ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = PaddingCustomValues.internalSpacing)
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = PaddingCustomValues.internalSpacing)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = PaddingCustomValues.internalSpacing),
                                text = if (task.dateWise) {
                                    "Repeated on the ${numFormatter(task.dateForReminder.dayOfMonth)} of every month"
                                } else if (task.repeatGapDuration == 0) {
                                    "Not repeated"
                                } else {
                                    "Repeated every ${getTextWithS("day", task.repeatGapDuration)}"
                                }
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = PaddingCustomValues.internalSpacing)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            // TODO: set icon to show how much we can snooze
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = PaddingCustomValues.internalSpacing),
                                text = getTextWithS(unit = "minute", num = task.snoozeDuration)
                                        + " or " +
                                        getTextWithS(unit = "day", num = task.postponeDuration)
                            )
                        }
                    )
                }
            )
        }
    )
}

enum class TimeFilter {
    DAY, WEEK, MONTH
}