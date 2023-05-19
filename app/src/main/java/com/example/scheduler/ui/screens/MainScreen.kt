package com.example.scheduler.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scheduler.values.PaddingCustomValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(toAddTaskScreen: () -> Unit) {
    val lazyListState = rememberLazyListState()
    val showFullText = remember { mutableStateOf(true) }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect { showFullText.value = (it == 0) }
    }
    Scaffold(
        floatingActionButton = { AddTaskFAB(showFullText = showFullText.value, toAddTaskScreen) },
        content = {
            ListOfTasks(
                padding = it,
                lazyListState = lazyListState
            )
        }
    )
}

@Composable
fun AddTaskFAB(showFullText: Boolean, toAddTaskScreen: () -> Unit) {
    FloatingActionButton(
        onClick = {
            // TODO: move to add tasks page
            toAddTaskScreen()
        },
        shape = CircleShape,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(PaddingCustomValues.externalSpacing)
            ) {
                AnimatedVisibility(
                    visible = showFullText,
                    modifier = Modifier.padding(start = PaddingCustomValues.externalSpacing),
                ) {
                    Text(text = "Add Task")
                }
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.Blue
                )
            }
        }
    )
}

@Composable
fun ListOfTasks(padding: PaddingValues, lazyListState: LazyListState) {
    // TODO: use lazy list to display tasks after sorting and remove this sample column
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        state = lazyListState,
        contentPadding = PaddingValues(vertical = PaddingCustomValues.internalSpacing)
    ) {
        // TODO: show all tasks
        items(
            count = 20,
            itemContent = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingCustomValues.internalSpacing)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = PaddingCustomValues.externalSpacing,
                                vertical = 15.dp
                            ),
                        text = "sample text $it",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        )
    }
}