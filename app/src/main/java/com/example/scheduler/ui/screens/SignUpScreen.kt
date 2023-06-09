package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.scheduler.R
import com.example.scheduler.values.FontSizeCustomValues
import com.example.scheduler.values.PaddingCustomValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// TODO:

@Composable
@Preview(showBackground = true)
fun SignUpScreenPrev() {
    SignUpScreen(
        googleSignInButton = { m, os, of ->
            ElevatedButton(
                onClick = { /*TODO*/ },
                content = {
                    Icon(
                        modifier = Modifier.padding(end = PaddingCustomValues.mediumSpacing),
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                    Text(text = "Google Sign In")
                }
            )
        },
        navigateToMainScreen = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    googleSignInButton: @Composable (
        modifier: Modifier,
        onSuccess: () -> Unit,
        onFailure: (issue: String) -> Unit,
    ) -> Unit,
    navigateToMainScreen: () -> Unit
) {
    val snackBarHostState = SnackbarHostState()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(it),
                contentAlignment = Alignment.Center,
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        verticalArrangement = Arrangement.spacedBy(PaddingCustomValues.mediumSpacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(PaddingCustomValues.screenGap))
//                                    .clip(CircleShape)
                                    .background(Color.Cyan),
                                content = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentDescription = null
                                    )
                                }
                            )
                            Text(
                                text = "Sign Up",
                                fontSize = FontSizeCustomValues.extraLarge
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = PaddingCustomValues.screenGap),
                                textAlign = TextAlign.Center,
                                text = "Use Google sign up to complete the sign up process"
                            )
                            val scope = CoroutineScope(Dispatchers.IO)
                            googleSignInButton(Modifier, navigateToMainScreen) {
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = "Failed to Login: $it",
                                        withDismissAction = true
                                    )
                                }
                            }
                        }
                    )
                }
            )
        }
    )
}