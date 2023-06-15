package com.example.scheduler.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduler.firebase.AccountFunctions
import com.example.scheduler.firebase.DatabaseFunctions
import com.example.scheduler.ui.prompt.ShowLoadingPrompt
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreenPrev() {
    SettingsScreen(
        navigateUp = {},
        navigateToSignUpScreen = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigateUp: () -> Unit, navigateToSignUpScreen: () -> Unit) {
    val snackBarHostState = SnackbarHostState()
    val showSnackBar: (String) -> Unit = { msg: String ->
        CoroutineScope(Dispatchers.IO).launch {
            snackBarHostState.showSnackbar(
                message = msg,
                withDismissAction = true
            )
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        topBar = { SettingsTopBar(navigateUp = navigateUp) },
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(PaddingCustomValues.cardSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it)
                    .padding(horizontal = PaddingCustomValues.cardSpacing)
                    .verticalScroll(ScrollState(0)),
                content = {
                    val showLoading = remember { mutableStateOf(false) }
                    val deleteAccStr = "Deleting account..."
                    val clearingTaskListStr = "Clearing Tasks..."
                    val loadingText = remember { mutableStateOf(deleteAccStr) }
                    if (showLoading.value) {
                        ShowLoadingPrompt(text = loadingText.value)
                    }
                    ConfirmationCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Delete Account?",
                        warningMessage = "Deletion of account would delete any and all data " +
                                "related to the account. This is an irreversible action.",
                        buttonText = "Delete Account",
                        onClick = {
                            loadingText.value = deleteAccStr
                            showLoading.value = true
                            AccountFunctions.deleteUserAccount(
                                notifyUser = showSnackBar,
                                onSuccess = navigateToSignUpScreen,
                                dismissLoadingPrompt = { showLoading.value = false }
                            )
                        }
                    )
                    ConfirmationCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "Log Out?",
                        warningMessage = "Logging out would remove all reminders but, Would not " +
                                "delete any tasks. Logging back in would set remaining reminders " +
                                "back. This means that while logged out, you would not be reminded " +
                                "of any pending tasks",
                        buttonText = "Log Out",
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navigateToSignUpScreen()
                            // TODO: navigate to signUp screen
                        }
                    )
                    ConfirmationCard(
                        title = "Clear all tasks?",
                        warningMessage = "Clearing all tasks would remove every task and reminder from database.",
                        buttonText = "Clear",
                        onClick = {
                            loadingText.value = clearingTaskListStr
                            showLoading.value = true
                            DatabaseFunctions.deleteAllTasks(
                                notifyUser = showSnackBar,
                                dismissLoadingPrompt = { showLoading.value = false }
                            )
                        }
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    navigateUp: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
                content = {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            )
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Settings"
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
fun ConfirmationCardPrev() {
    ConfirmationCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Delete Account?",
        warningMessage = "Deletion of account would delete any and all data related to the account. This is an irreversible action.",
        buttonText = "Delete Account",
        onClick = {}
    )
}

@Composable
fun ConfirmationCard(
    modifier: Modifier = Modifier,
    title: String,
    warningMessage: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingCustomValues.mediumSpacing),
                content = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        fontSize = FontSizeCustomValues.large
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = warningMessage
                    )
                    val checked = remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Checkbox(
                                checked = checked.value,
                                onCheckedChange = {
                                    checked.value = checked.value.not()
                                }
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "I understand",
                                fontSize = FontSizeCustomValues.medium
                            )
                        }
                    )
                    Button(
                        enabled = checked.value,
                        modifier = Modifier.align(Alignment.End),
                        onClick = onClick,
                        content = {
                            Text(text = buttonText)
                        }
                    )
                }
            )
        }
    )
}