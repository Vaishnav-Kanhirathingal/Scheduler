package com.example.scheduler.ui.prompt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.scheduler.data.Task
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DeleteTaskPrompt(
    taskDoc: DocumentSnapshot,
    snackBarHostState: SnackbarHostState,
    showDeletePrompt: MutableState<Boolean>,
    refreshList: () -> Unit
) {
    val task = Task.fromDocument(taskDoc)
    val deleting = remember { mutableStateOf(false) }
    if (showDeletePrompt.value) {
        val onCancel = { showDeletePrompt.value = false }
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
                                    modifier = Modifier
                                        .padding(
                                            start = PaddingCustomValues.mediumSpacing,
                                            end = PaddingCustomValues.mediumSpacing,
                                            bottom = PaddingCustomValues.largeSpacing
                                        )
                                        .fillMaxWidth(),
                                    text = "Do you want to delete the task with title \"${task.title}\"?",
                                    fontSize = FontSizeCustomValues.large
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(PaddingCustomValues.mediumSpacing),
                                    modifier = Modifier.fillMaxWidth(),
                                    content = {
                                        OutlinedButton(
                                            modifier = Modifier.weight(1f),
                                            onClick = onCancel,
                                            content = {
                                                Text(text = "cancel")
                                            }
                                        )
                                        Button(
                                            modifier = Modifier.weight(1f),
                                            onClick = {
                                                showDeletePrompt.value = false
                                                deleting.value = true
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
                                                        refreshList()
                                                    },
                                                    onFailureListener = {
                                                        deleting.value = false
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            snackBarHostState.showSnackbar(
                                                                message = "Failed to deleted task: $it",
                                                                withDismissAction = true
                                                            )
                                                        }
                                                    }
                                                )
                                            },
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
    if (deleting.value) {
        ShowLoadingPrompt(text = "deleting...")
    }
}


@Composable
@Preview(showBackground = true)
fun ShowLoadingPromptPrev() {
    ShowLoadingPrompt(text = "Saving...")
}

@Composable
fun ShowLoadingPrompt(text: String) {
    Dialog(
        onDismissRequest = {},
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(PaddingCustomValues.screenGap)
                            .align(Alignment.CenterHorizontally),
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = PaddingCustomValues.screenGap),
                        text = text,
                        textAlign = TextAlign.Center,
                        fontSize = FontSizeCustomValues.extraLarge
                    )
                }
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}
