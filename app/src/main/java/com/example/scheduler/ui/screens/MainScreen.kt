package com.example.scheduler.ui.screens

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scheduler.data.Repetitions
import com.example.scheduler.data.Reps
import com.example.scheduler.data.StringFunctions.getDateAsText
import com.example.scheduler.data.StringFunctions.getTextWithS
import com.example.scheduler.data.StringFunctions.getTimeAsText
import com.example.scheduler.data.StringFunctions.numFormatter
import com.example.scheduler.data.testTaskList
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.values.ColorCustomValues
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
    val drawerState = DrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope(getContext = { Dispatchers.IO })

    val showFullText = remember { mutableStateOf(true) }

    val filter = remember { mutableStateOf(Repetitions.ALL) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { showFullText.value = (it == 0) }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f)
                    .background(Color.White),
            )
        },
        content = {
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
                                modifier = Modifier,
                                onSuccess = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        snackBarHostState.showSnackbar(
                                            message = "Login Successful",
                                            withDismissAction = true
                                        )
                                    }
                                },
                                onFailure = {
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
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = null,
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
                    val receivedList = remember { mutableStateListOf<DocumentSnapshot>() }
                    Column {
                        DatabaseFunctions.getListOfTasksAsDocuments(
                            listReceiver = { ds ->
                                receivedList.clear()
                                for (i in ds) {
                                    val task = DatabaseFunctions.getTaskFromDocument(i)
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
                        FilterRow(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxWidth(),
                            filterSelected = filter
                        )
                        SavedTaskList(
                            modifier = Modifier.fillMaxWidth(),
                            lazyListState = lazyListState,
                            listOfTaskDocumentsReceived = receivedList,
                            selected = filter,
                            snackBarHostState = snackBarHostState
                        )
                    }
                }
            )
        }
    )
}


@Composable
fun DrawerContent(modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val loginErrorMessage = "Login First"
    Box(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(ScrollState(0))
                    .padding(horizontal = PaddingCustomValues.mediumSpacing),
                content = {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(top = PaddingCustomValues.mediumSpacing)
                            .border(width = 1.dp, color = Color.Black),
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(auth.currentUser?.photoUrl)
                            .build(),
                        contentDescription = null
                    )
                    DetailsRow(
                        text = auth.currentUser?.email ?: loginErrorMessage,
                        icon = Icons.Filled.Email
                    )
                    DetailsRow(
                        text = auth.currentUser?.displayName ?: loginErrorMessage,
                        icon = Icons.Filled.AccountBox
                    )
                    OptionMenu()
                }
            )
        }
    )

}

@Composable
@Preview(showBackground = true)
fun OptionMenuPrev() {
    OptionMenu(modifier = Modifier.padding(PaddingCustomValues.mediumSpacing))
}

@Composable
fun OptionMenu(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PaddingCustomValues.menuItemMargin),
        modifier = modifier,
        content = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = PaddingCustomValues.screenGap,
                        start = PaddingCustomValues.menuTextSpacing
                    ),
                text = "Account",
                fontSize = FontSizeCustomValues.menuTitle
            )
            MenuItem(
                icon = Icons.Default.AccountBox,
                text = "Sign-out",
                onClick = {
                    // TODO: to settings page
                }
            )
            TitledSeparator(text = "Today's Tasks")
            // TODO: show a list of today's tasks
            testTaskList.forEach {
                MenuTaskItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = it.title,
                    onCancel = { TODO("cancel and remove task from firebase") }
                )
            }
            TitledSeparator(text = "About")
            MenuItem(
                icon = Icons.Default.Info,
                text = "Documentation",
                onClick = {
                    // TODO: to settings page
                }
            )
            MenuItem(
                icon = Icons.Default.Info,
                text = "Git-Hub Releases",
                onClick = { TODO("open github") }
            )
            TitledSeparator(text = "Exit")
            MenuItem(
                icon = Icons.Default.ExitToApp,
                text = "Exit",
                onClick = { TODO("exit app") }
            )
        }
    )
}

@Composable
fun TitledSeparator(text: String) {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        thickness = PaddingCustomValues.lineThickness
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = PaddingCustomValues.menuTextSpacing,
                top = PaddingCustomValues.largeSpacing
            ),
        fontSize = FontSizeCustomValues.menuTitle,
        text = text
    )
}

@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        content = {
            Icon(
                modifier = Modifier.padding(end = PaddingCustomValues.mediumSpacing),
                imageVector = icon,
                contentDescription = null,
                tint = ColorCustomValues.sideMenuIconColor
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                color = ColorCustomValues.sideMenuTextColor
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MenuTaskItemPrev() {
    MenuTaskItem(
        modifier = Modifier.fillMaxWidth(),
        text = testTaskList[0].title,
        onCancel = {}
    )
}

@Composable
fun MenuTaskItem(
    modifier: Modifier = Modifier,
    text: String,
    onCancel: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = PaddingCustomValues.largeSpacing,
                        vertical = PaddingCustomValues.mediumSpacing
                    ),
                text = text
            )
            IconButton(
                onClick = onCancel,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null
                    )
                }
            )
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
    selected: MutableState<Reps>,
    snackBarHostState: SnackbarHostState
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        content = {
            items(
                count = listOfTaskDocumentsReceived.size,
                itemContent = {
                    val task =
                        DatabaseFunctions.getTaskFromDocument(listOfTaskDocumentsReceived[it])
                    val doc = listOfTaskDocumentsReceived[it]
                    if (task.isScheduledIn(selected.value.step) || selected.value == Repetitions.ALL) {
                        DetailedTaskCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(PaddingCustomValues.smallSpacing),
                            taskDoc = doc,
                            snackBarHostState = snackBarHostState
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun DetailedTaskCard(
    snackBarHostState: SnackbarHostState,
    taskDoc: DocumentSnapshot,
    modifier: Modifier = Modifier
) {
    // TODO: add a UI element that tells how much time remaining till the next alarm
    val task = DatabaseFunctions.getTaskFromDocument(taskDoc)
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
                                imageVector =
                                when {
                                    task.isScheduledIn(Repetitions.DAY.step) -> Icons.Outlined.Build
                                    task.isScheduledIn(Repetitions.WEEK.step) -> Icons.TwoTone.Build
                                    task.isScheduledIn(Repetitions.MONTH.step) -> Icons.Filled.Build
                                    else -> Icons.Outlined.Warning
                                },
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
                                onClick = { TODO("move to edit page for the selected task") },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = null
                                    )
                                }
                            )
                            //-------------------------------------------------------deleting prompt
                            val showDeletePrompt = remember { mutableStateOf(false) }
                            val deleting = remember { mutableStateOf(false) }
                            if (showDeletePrompt.value) {
                                QuestionPrompt(
                                    onConfirm = { /*TODO*/
                                        deleting.value = true
                                        showDeletePrompt.value = false
                                        DatabaseFunctions.deleteTaskDocument(
                                            taskDoc = taskDoc,
                                            onSuccessListener = {
                                                deleting.value = false
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    snackBarHostState.showSnackbar(
                                                        message = "Successfully deleted task",
                                                        withDismissAction = true
                                                    )
                                                }
                                                // TODO: show snack bar
                                            },
                                            onFailureListener = {
                                                deleting.value = false
                                                // TODO:
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    snackBarHostState.showSnackbar(
                                                        message = "Failed to deleted task: $it",
                                                        withDismissAction = true
                                                    )
                                                }
                                            }
                                        )
                                    },
                                    onCancel = { /*TODO*/
                                        showDeletePrompt.value = false
                                    },
                                    question = "delete task with title \"${task.title}\""
                                )
                            }

                            if (deleting.value) {
                                ShowLoadingPrompt(text = "deleting...")
                            }
                            //-----------------------------------------------------------------close
                            IconButton(
                                onClick = { showDeletePrompt.value = true },
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
                        text = getTextWithS(unit = "minute", num = task.snoozeDuration) + " or " +
                                getTextWithS(unit = "day", num = task.postponeDuration),
                        icon = Icons.Outlined.ArrowForward
                    )
                }
            )
        }
    )
}

@Composable
fun DetailsRow(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(top = PaddingCustomValues.smallSpacing)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Icon(imageVector = icon, contentDescription = null)
            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = PaddingCustomValues.smallSpacing)
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
fun QuestionPromptPrev() {
    QuestionPrompt(
        onConfirm = { /*TODO*/ },
        onCancel = { /*TODO*/ },
        question = "do you want to delete the selected task?"
    )
}

@Composable
fun QuestionPrompt(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    question: String
) {
    Dialog(
        onDismissRequest = onCancel,
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingCustomValues.mediumSpacing),
                        content = {
                            Text(
                                modifier = Modifier.padding(horizontal = PaddingCustomValues.mediumSpacing),
                                text = question,
                                fontSize = FontSizeCustomValues.medium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                content = {
                                    TextButton(
                                        modifier = Modifier.weight(1f),
                                        onClick = onCancel,
                                        content = {
                                            Text(text = "cancel")
                                        }
                                    )
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = onConfirm,
                                        content = {
                                            Text(text = "confirm")
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}