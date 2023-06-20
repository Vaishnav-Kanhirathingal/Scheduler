package com.example.scheduler.ui.screens.main_screen

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scheduler.R
import com.example.scheduler.data.Repetitions
import com.example.scheduler.data.Reps
import com.example.scheduler.data.StringFunctions.getDateAsText
import com.example.scheduler.data.StringFunctions.getTextWithS
import com.example.scheduler.data.StringFunctions.getTimeAsText
import com.example.scheduler.data.StringFunctions.numFormatter
import com.example.scheduler.data.Task
import com.example.scheduler.data.TestValues
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.ui.prompt.DeleteTaskPrompt
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(toAddTaskScreen: () -> Unit, toSettingsPage: () -> Unit) {
    val snackBarHostState = SnackbarHostState()
    val lazyListState = rememberLazyListState()
    val drawerState = DrawerState(DrawerValue.Closed)

    val showFullText = remember { mutableStateOf(true) }

    val filter = remember { mutableStateOf(Repetitions.ALL) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { showFullText.value = (it == 0) }
    }
    val receivedList = remember { mutableStateListOf<DocumentSnapshot>() }
    val refreshList: () -> Unit = {
        DatabaseFunctions.getListOfTasksAsDocuments(
            listReceiver = { listOfDocumentSnapshots ->
                receivedList.clear()
                for (i in listOfDocumentSnapshots) {
                    val task = Task.fromDocument(i)
                    val ret = receivedList.add(i)
                    Log.d(
                        TAG, "added: $ret = " +
                                GsonBuilder().setPrettyPrinting().create()
                                    .toJson(task)
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
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .background(Color.White),
                receivedList = receivedList,
                snackBarHostState = snackBarHostState,
                refreshList = refreshList,
                toSettingsPage = toSettingsPage
            )
        },
        content = {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                topBar = { MainScreenAppBar(drawerState = drawerState) },
                floatingActionButton = {
                    AddTaskFAB(
                        showFullText = showFullText.value,
                        toAddTaskScreen = toAddTaskScreen
                    )
                },
                content = {
                    Column {
                        LaunchedEffect(key1 = receivedList, block = { refreshList() })
                        FilterRow(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxWidth(),
                            filterSelected = filter
                        )
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = PaddingCustomValues.lineThickness
                        )
                        SavedTaskList(
                            modifier = Modifier.fillMaxWidth(),
                            lazyListState = lazyListState,
                            listOfTaskDocumentsReceived = receivedList,
                            filter = filter,
                            snackBarHostState = snackBarHostState,
                            refreshList = refreshList,
                        )
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenAppBar(drawerState: DrawerState) {
    val scope = rememberCoroutineScope(getContext = { Dispatchers.IO })
    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = "Task List",
                textAlign = TextAlign.Center
            )
        },
        actions = {
            val imageModifier = Modifier
                .clip(CircleShape)
                .border(width = 1.dp, color = Color.Black, shape = CircleShape)
            AsyncImage(
                modifier = imageModifier,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(FirebaseAuth.getInstance().currentUser!!.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { scope.launch { drawerState.open() } },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null,
                    )
                }
            )
        },
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
fun FilterRow(
    modifier: Modifier = Modifier,
    filterSelected: MutableState<Reps>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingCustomValues.smallSpacing),
        modifier = modifier
            .padding(horizontal = PaddingCustomValues.smallSpacing)
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
        onClick = toAddTaskScreen,
        icon = {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
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
    listOfTaskDocumentsReceived: SnapshotStateList<DocumentSnapshot>,
    filter: MutableState<Reps>,
    snackBarHostState: SnackbarHostState,
    refreshList: () -> Unit
) {
    val filteredList = mutableListOf<DocumentSnapshot>()
    listOfTaskDocumentsReceived.forEach {
        val task = Task.fromDocument(it)
        when (filter.value) {
            Repetitions.ALL -> filteredList.add(it)
            Repetitions.DAY -> if (task.isScheduledForToday()) filteredList.add(it)
            Repetitions.WEEK -> if (task.isScheduledIn(Repetitions.WEEK.step)) filteredList.add(it)
            Repetitions.MONTH -> if (task.isScheduledIn(Repetitions.MONTH.step)) filteredList.add(it)
            else -> throw Exception("out of order filter")
        }
    }
    if (filteredList.isEmpty()) {
        ListEmptyText(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(30.dp)
        )
    } else {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            content = {
                items(
                    count = filteredList.size,
                    itemContent = {
                        val taskDoc = filteredList[it]
                        val task = Task.fromDocument(filteredList[it])

                        val showDeletePrompt = remember { mutableStateOf(false) }
                        DeleteTaskPrompt(
                            taskDoc = taskDoc,
                            snackBarHostState = snackBarHostState,
                            showDeletePrompt = showDeletePrompt,
                            refreshList = refreshList
                        )

                        DetailedTaskCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PaddingCustomValues.smallSpacing),
                            task = task,
                            onDelete = { showDeletePrompt.value = true }
                        )
                    }
                )
            }
        )
    }
}

@Preview
@Composable
fun DetailedTaskCardPrev() {
    DetailedTaskCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingCustomValues.smallSpacing),
        task = TestValues.testTaskList[0],
        onDelete = {}
    )
}

@Composable
fun DetailedTaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.padding(PaddingCustomValues.mediumSpacing),
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.task_24),
                                contentDescription = null
                            )
                            Text(
                                text = task.title,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = PaddingCustomValues.mediumSpacing),
                                fontSize = FontSizeCustomValues.large
                            )
                            IconButton(
                                onClick = onDelete,
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
                    DetailsRow(
                        text = task.description,
                        DetailIcon = {
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        }
                    )
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
                        DetailIcon = {
                            Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null)
                        }
                    )
                    DetailsRow(
                        text = if (task.dateWise) {
                            "Repeated on the ${numFormatter(task.dateForReminder.dayOfMonth)} of every month"
                        } else if (task.repeatGapDuration == 0) {
                            "Not repeated"
                        } else {
                            "Repeated every ${getTextWithS("day", task.repeatGapDuration)}"
                        },
                        DetailIcon = {
                            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                        }
                    )
                    DetailsRow(
                        text = getTextWithS(unit = "day", num = task.postponeDuration),
                        DetailIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.skip_next_24),
                                contentDescription = null
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun DetailsRow(
    text: String,
    DetailIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = PaddingCustomValues.smallSpacing)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            DetailIcon()
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = PaddingCustomValues.smallSpacing)
            )
        }
    )
}