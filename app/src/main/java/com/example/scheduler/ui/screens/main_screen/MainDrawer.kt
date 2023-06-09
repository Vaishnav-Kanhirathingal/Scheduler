package com.example.scheduler.ui.screens.main_screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.scheduler.R
import com.example.scheduler.data.Task
import com.example.scheduler.data.TestValues
import com.example.scheduler.ui.prompt.DeleteTaskPrompt
import com.example.scheduler.values.ColorCustomValues
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    receivedList: SnapshotStateList<DocumentSnapshot>,
    snackBarHostState: SnackbarHostState,
    refreshList: () -> Unit,
    toSettingsPage: () -> Unit,
    toAppInfoScreen: () -> Unit,
) {
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
                        DetailIcon = {
                            Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                        }
                    )
                    DetailsRow(
                        text = auth.currentUser?.displayName ?: loginErrorMessage,
                        DetailIcon = {
                            Icon(imageVector = Icons.Filled.AccountBox, contentDescription = null)
                        }
                    )
                    OptionMenu(
                        modifier = Modifier.fillMaxWidth(),
                        receivedList = receivedList,
                        snackBarHostState = snackBarHostState,
                        refreshList = refreshList,
                        toSettingsPage = toSettingsPage,
                        toAppInfoScreen = toAppInfoScreen
                    )
                }
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
@Preview(showBackground = true, widthDp = 250)
fun OptionMenuPrev() {
    OptionMenu(
        receivedList = remember { mutableStateListOf() },
        snackBarHostState = SnackbarHostState(),
        refreshList = {},
        toSettingsPage = {},
        toAppInfoScreen = {}
    )
}

@Composable
fun OptionMenu(
    modifier: Modifier = Modifier,
    receivedList: SnapshotStateList<DocumentSnapshot>,
    snackBarHostState: SnackbarHostState,
    refreshList: () -> Unit,
    toSettingsPage: () -> Unit,
    toAppInfoScreen: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(PaddingCustomValues.menuItemMargin),
        modifier = modifier,
        content = {
            val context = LocalContext.current
            val openUrl = { url: String ->
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = PaddingCustomValues.screenGap,
                        start = PaddingCustomValues.menuTextSpacing
                    ),
                text = "Today's Tasks",
                fontSize = FontSizeCustomValues.menuTitle
            )
            MenuTaskList(
                receivedList = receivedList,
                snackBarHostState = snackBarHostState,
                refreshList = refreshList
            )
            TitledSeparator(text = "Settings")
            MenuItem(
                icon = Icons.Default.Settings,
                text = "Settings",
                onClick = toSettingsPage
            )
            MenuItem(
                icon = Icons.Default.Info,
                text = "App Info",
                onClick = toAppInfoScreen
            )
            TitledSeparator(text = "About")
            MenuItem(
                iconPainter = painterResource(id = R.drawable.developer_guide_24),
                text = "Documentation",
                onClick = { openUrl("https://github.com/Vaishnav-Kanhirathingal/Scheduler/blob/main/README.md") }
            )
            MenuItem(
                iconPainter = painterResource(id = R.drawable.github_icon_24),
                text = "Git-Hub Releases",
                onClick = { openUrl("https://github.com/Vaishnav-Kanhirathingal/Scheduler/releases") }
            )
            TitledSeparator(text = "Exit")
            MenuItem(
                icon = Icons.Default.ExitToApp,
                text = "Exit",
                onClick = {
                    val act = context as Activity
                    act.finishAffinity()
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MenuItemPrev() {
    MenuItem(
        icon = Icons.Filled.AccountCircle,
        text = "something",
        onClick = {}
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
                imageVector = icon,
                contentDescription = null,
                tint = ColorCustomValues.sideMenuIconColor
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingCustomValues.mediumSpacing),
                text = text,
                color = ColorCustomValues.sideMenuTextColor
            )
        }
    )
}

@Composable
fun MenuItem(
    iconPainter: Painter,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        content = {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = ColorCustomValues.sideMenuIconColor
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PaddingCustomValues.mediumSpacing),
                text = text,
                color = ColorCustomValues.sideMenuTextColor
            )
        }
    )
}

@Composable
fun MenuTaskList(
    receivedList: SnapshotStateList<DocumentSnapshot>,
    snackBarHostState: SnackbarHostState,
    refreshList: () -> Unit
) {
    val newList = mutableListOf<DocumentSnapshot>()
    receivedList.forEach {
        val task = Task.fromDocument(it)
        if (task.isScheduledForToday()) {
            newList.add(it)
        }
    }
    if (newList.isNotEmpty()) {
        newList.forEach {
            val task = Task.fromDocument(it)
            if (task.isScheduledForToday()) {
                val showDeletePrompt = remember { mutableStateOf(false) }
                DeleteTaskPrompt(
                    taskDoc = it,
                    snackBarHostState = snackBarHostState,
                    showDeletePrompt = showDeletePrompt,
                    refreshList = refreshList
                )
                MenuTaskItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = task.title,
                    onDelete = {
                        showDeletePrompt.value = true
                    }
                )
            }
        }
    } else {
        ListEmptyText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingCustomValues.screenGap)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuTaskItemPrev() {
    MenuTaskItem(
        modifier = Modifier.fillMaxWidth(),
        text = TestValues.testTaskList[0].title,
        onDelete = {}
    )
}

@Composable
fun MenuTaskItem(
    modifier: Modifier = Modifier,
    text: String,
    onDelete: () -> Unit
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
}

@Preview(showBackground = true)
@Composable
fun ListEmptyTextPrev() {
    ListEmptyText(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}

@Composable
fun ListEmptyText(modifier: Modifier = Modifier) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center,
        content = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "No task scheduled for today, Add a task using the \"Add Task\" button",
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    )
}